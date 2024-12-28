package org.am.com.blockchain.miner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.am.com.balance.UTXO;
import org.am.com.block.Block;
import org.am.com.block.Header;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.service.BlockChainService;
import org.am.com.blockchain.util.MerkleRootUtil;
import org.am.com.tx.TX;
import org.am.com.tx.TXBuilder;
import org.am.com.user.Key;
import org.am.com.util.BlockSizeCalculator;
import org.am.com.util.crypto.CryptoUtil;
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
        String cbtxid = "miner_v1_set_cb_txid_" + count.get();
        TX coinbase = TX.generateCoinBaseTX(cbtxid, key.getAddress(), COINBASE);
        txs.add(coinbase);
        for (int i = 0; i < validMempoolTransactions.size(); i++) {
            MempoolTransaction mpt = validMempoolTransactions.get(i);
            List<UTXO> utxos = mpt.getTxCoversSum();
            String txid = "miner_set_txid_" + count.get();
            TX tx = TXBuilder
                    .generateTX(mpt.getSender(), mpt.getRecipient(), utxos, mpt.getAmount(), mpt.getChange(), txid);
            txs.add(tx);
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
