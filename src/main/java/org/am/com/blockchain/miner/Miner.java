package org.am.com.blockchain.miner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.*;
import org.am.com.blockchain.model.user.Key;
import org.am.com.blockchain.service.BlockChainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class Miner {
    private final BlockChainService blockChainService;
    private final AtomicInteger count = new AtomicInteger(0);
    private final int BATCH_SIZE = 2;
    private final double COINBASE = 3;
    private final Key key;
    private final ObjectMapper objectMapper;
    private final String minerFileName;

    public Miner(BlockChainService blockChainService,
                 ObjectMapper objectMapper,
                 @Value("${miner.data}") String minerFileName) throws IOException {
        this.blockChainService = blockChainService;
        this.minerFileName = minerFileName;
        this.objectMapper = objectMapper;
        key = loadMinerJson();
    }

    private Key loadMinerJson() throws IOException {
        ClassPathResource resource = new ClassPathResource(minerFileName);
        Key data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
        log.info("Loaded miner JSON Data: " + data);
        return data;
    }

    @Scheduled(fixedDelay = 20000, initialDelay = 10000)
    private void invokeMiner() {
        int currentCount = count.incrementAndGet();
        log.info("Mining started. count: {}", currentCount);
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
        log.info("Mining finished., count: {}", currentCount);
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
        TxOutEntry txOutEntry = new TxOutEntry(COINBASE, key.getAddress(), 0);
        return new TX(txid, List.of(txInEntry), List.of(txOutEntry));
    }

    private boolean isValid(MempoolTransaction transaction) {
        return true;
    }

    private int solvePuzzle() {
        int solved = 0;
        try {
            log.info("Solving puzzle");
            Thread.sleep(10000);
            solved = count.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Solving interrupted", e);
        } finally {
            log.info("Solving finished. count: {}", solved);
        }
        return solved;
    }
}
