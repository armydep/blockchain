package org.am.com.blockchain.miner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.*;
import org.am.com.blockchain.model.user.Key;
import org.am.com.blockchain.service.BlockChainService;
import org.am.com.blockchain.util.BlockSizeCalculator;
import org.am.com.blockchain.util.MerkleRootUtil;
import org.am.com.blockchain.util.crypto.CryptoUtil;
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
    private final int DIFFICULTY = 6;
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
        //1. Body - tx
        final List<TX> txs = buildTX(validMempoolTransactions);
        //2. Header
        Header header = createHeader(new ArrayList<>(txs));
        return new Block(header, txs);
    }

    private List<TX> buildTX(List<MempoolTransaction> validMempoolTransactions) {
        List<TX> txs = new ArrayList<>();
        TX coinbase = generateCoinBaseTX(count.get());
        txs.add(coinbase);
        for (int i = 0; i < validMempoolTransactions.size(); i++) {
            txs.add(generateTX(validMempoolTransactions.get(i), "txid_" + count.get()));
        }
        return txs;
    }

    public record MinerData(String previousHash, String merkleRoot, long timestamp, int index) {
    }

    private Header createHeader(List<TX> txs) {
        Block previousBlock = blockChainService.getLatestBlock();
        int index = previousBlock.getIndex() + 1;
        String previousHash = previousBlock.getHash();
        long timestamp = System.currentTimeMillis() / 1000;
        String merkleRoot = MerkleRootUtil.createMerkleRoot(txs.stream().map(TX::toString).toList());
        MinerData mdata = new MinerData(previousHash, merkleRoot, timestamp, index);
        Header minedHeader = mine(mdata, DIFFICULTY);
        int size = BlockSizeCalculator.calculateBlockSize(minedHeader, txs);
        return new Header(minedHeader, size);
    }

    private TX generateTX(MempoolTransaction mpTx, String txid) {
        List<TxInEntry> txInEntries = createTxInFromUTXOs(mpTx.getTxCoversSum());
        List<TxOutEntry> txOutEntries = new ArrayList<>();
        TxOutEntry txOutEntry = new TxOutEntry(mpTx.getAmount(), mpTx.getRecipient(), 0);
        txOutEntries.add(txOutEntry);
        if (mpTx.getChange() > 0) {
            TxOutEntry txOutEntryChange = new TxOutEntry(mpTx.getChange(), mpTx.getSender(), 1);
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

    private Header mine(MinerData data, final int difficulty) {
        log.info("Mining started. Difficulty: {}", difficulty);
        long startTime = System.currentTimeMillis();
        String target = "0".repeat(difficulty);
        int nonce = 0;
        String hash;
        Header header;
        while (true) {
            header = buildHeader(data, nonce);
            hash = CryptoUtil.generateSHA256(header.toString());
            if (hash.startsWith(target)) {
                log.info("Block mined with nonce: " + nonce);
                break;
            }
            nonce++;
        }
        log.info("Mining finished. Time took: {}", (System.currentTimeMillis() - startTime) / 1000);
        return new Header(header, hash);
    }

    private Header buildHeader(MinerData data, int nonce) {
        return new Header(null, data.previousHash, data.merkleRoot, nonce, data.timestamp, data.index, null);
    }
}
