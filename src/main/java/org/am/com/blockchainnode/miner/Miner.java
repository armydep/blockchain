package org.am.com.blockchainnode.miner;

import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.model.MempoolTransaction;
import org.am.com.blockchainnode.model.block.Block;
import org.am.com.blockchainnode.model.block.TX;
import org.am.com.blockchainnode.model.block.TxInEntry;
import org.am.com.blockchainnode.model.block.TxOutEntry;
import org.am.com.blockchainnode.service.BlockChainService;
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
    private final float COINBASE = 3f;

    public Miner(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
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
            blockChainService.updateUTXO();
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
        String previousHash = previousBlock.getPreviousHash();
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
        TxInEntry txInEntry = new TxInEntry(mpTx.getTxid(), 0, null);
        TxOutEntry txOutEntry = new TxOutEntry(COINBASE, MY_ADDRESS, 0);
        return new TX(txid, List.of(txInEntry), List.of(txOutEntry));
    }

    private TX generateCoinBaseTX(int i) {
        String txid = "txid_cb_" + i;
        TxInEntry txInEntry = new TxInEntry(txid, 0, "true");
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
            log.info("Solved puzzle {}", solved);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mining interrupted", e);
        } finally {
            mining = false;
            log.info("Mining finished: {}, count: {}", mining, solved);
        }
        return solved;
    }
}
