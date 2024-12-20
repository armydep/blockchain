package org.am.com.blockchain.miner;

import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.*;
import org.am.com.blockchain.service.BlockChainService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class Miner {
    private final BlockChainService blockChainService;
    private volatile Boolean mining = false;
    private final AtomicInteger count = new AtomicInteger(0);
    private final int BATCH_SIZE = 2;
    private final String MY_ADDRESS = "mineraddr127001";
    private final double COINBASE = 3d;

    public Miner(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    @Scheduled(fixedDelay = 20000, initialDelay = 10000)
    private void invokeMiner() {
        mining = true;
        int currentCount = count.incrementAndGet();
        log.info("Mining started: {}, count: {}", mining, currentCount);

        List<MempoolTransaction> mempoolTransactions = blockChainService.getBatch(BATCH_SIZE);
        List<MempoolTransaction> validMempoolTransactions = new ArrayList<>();
        for (int i = 0; i < mempoolTransactions.size(); i++) {
            MempoolTransaction transaction = mempoolTransactions.get(i);
            if (isValid(transaction)) {
                validMempoolTransactions.add(transaction);
            } else {
                log.info("Transaction: {} is not valid", transaction);
            }
        }
        if (!validMempoolTransactions.isEmpty()) {
            Block block = assemblyBlock(validMempoolTransactions);
            blockChainService.submitBlock(block);
            blockChainService.clearMempoolTX(validMempoolTransactions);
            blockChainService.updateUTXO(validMempoolTransactions);
        } else {
            log.info("No valid mempool transactions");
        }
        mining = false;
        log.info("Mining finished: {}, count: {}", mining, currentCount);
    }

    private Block assemblyBlock(List<MempoolTransaction> validMempoolTransactions) {
        Block previousBlock = blockChainService.getLatestBlock();
        String merkleRoot = "merkleRoot" + count.get();
        long timestamp = System.currentTimeMillis() / 1000;
        int nonce = solvePuzzle();
        int size = count.get();
        int index = previousBlock.getIndex() + 1;
        String previousHash = previousBlock.getHash();
        String hash = previousHash + timestamp + nonce + size + index + merkleRoot;
        List<TX> txs = new ArrayList<>();
        TX coinbase = generateCoinBaseTX(count.get());
        txs.add(coinbase);
        for (int i = 0; i < validMempoolTransactions.size(); i++) {
            txs.add(generateTX(validMempoolTransactions.get(i), "txid_" + count.get()));
        }
        return new Block(hash, previousHash, timestamp, nonce, index, txs);
    }

    private TX generateTX(MempoolTransaction mpTx, String txid) {
        List<TxInEntry> txInEntries = createTxInFromUTXOs(mpTx.getTxCoversSum());
        List<TxOutEntry> txOutEntries = new ArrayList<>();
        TxOutEntry txOutEntry = new TxOutEntry(mpTx.getAmount(), mpTx.getTo(), 0);
        txOutEntries.add(txOutEntry);
        if (mpTx.getChange() > 0) {
            TxOutEntry txOutEntryChange = new TxOutEntry(mpTx.getChange(), mpTx.getFrom(), 1);
            txOutEntries.add(txOutEntryChange);
        }
        return new TX(txid, txInEntries, txOutEntries);
    }

    private List<TxInEntry> createTxInFromUTXOs(List<UTXO> txCoversSum) {
        List<TxInEntry> txInEntries = new ArrayList<>();
        for (int i = 0; i < txCoversSum.size(); i++) {
            UTXO utxo = txCoversSum.get(i);
            TxInEntry txInEntry = new TxInEntry(utxo.getTx(), utxo.getVout(), null);
            txInEntries.add(txInEntry);
        }
        return txInEntries;
    }

    private TX generateCoinBaseTX(int i) {
        String txid = "txid_cb_" + i;
        TxInEntry txInEntry = new TxInEntry("", 0, "true");
        TxOutEntry txOutEntry = new TxOutEntry(COINBASE, MY_ADDRESS, 0);
        return new TX(txid, List.of(txInEntry), List.of(txOutEntry));
    }

    private boolean isValid(MempoolTransaction transaction) {
        return true;
    }

    private int solvePuzzle() {
        int solved = 0;
        try {
            log.info("Solving puzzle");
            Thread.sleep(20000);
            solved = count.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Solving interrupted", e);
        } finally {
            mining = false;
            log.info("Solving finished: {}, count: {}", mining, solved);
        }
        return solved;
    }
}
