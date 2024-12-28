package org.am.com.blockchain.miner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.am.com.block.Block;
import org.am.com.block.Header;
import org.am.com.user.Key;
import org.am.com.blockchain.service.BlockChainServiceV2;
import org.am.com.util.BlockSizeCalculator;
import org.am.com.blockchain.util.MerkleRootUtil;
import org.am.com.util.crypto.CryptoUtil;
import org.am.com.tx.TX;
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
public class MinerV2 {
    private final BlockChainServiceV2 blockChainService;
    private final AtomicInteger count = new AtomicInteger(0);
    private final int BATCH_SIZE = 2;
    private final double COINBASE = 3;
    private final int DIFFICULTY = 6;
    private final Key key;
    private final ObjectMapper objectMapper;
    private final String minerFileName;

    public MinerV2(BlockChainServiceV2 blockChainService,
                   ObjectMapper objectMapper,
                   @Value("${miner.data}") String minerFileName) throws IOException {
        this.blockChainService = blockChainService;
        this.minerFileName = minerFileName;
        this.objectMapper = objectMapper;
        key = loadMinerJson();
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    private void invokeMinerV2() {
        int currentCount = count.incrementAndGet();
        log.info("Mining started. count: {}", currentCount);
        List<TX> mempoolTXs = blockChainService.getMempoolBatch(BATCH_SIZE);
        if (!mempoolTXs.isEmpty()) {
            Block block = assemblyBlock(mempoolTXs);
            blockChainService.submitBlock(block);
        } else {
            log.info("No valid mempool transactions");
        }
        log.info("Mining finished., count: {}", currentCount);
    }

    private Block assemblyBlock(List<TX> txso) {
        String cbtxid = "miner_v2_set_cb_txid_" + count.get();
        TX coinbase = TX.generateCoinBaseTX(cbtxid, key.getAddress(), COINBASE);
        txso.addFirst(coinbase);
        Header header = mineAndCreateHeader(new ArrayList<>(txso));
        return new Block(header, txso);
    }

    public record MinerData(String previousHash, String merkleRoot, long timestamp, int index) {
    }

    private Header mineAndCreateHeader(List<TX> txs) {
        Block previousBlock = blockChainService.getLatestBlock();
        int index = previousBlock.getIndex() + 1;
        String previousHash = previousBlock.getHash();
        long timestamp = System.currentTimeMillis() / 1000;
        List<String> modifiableTxsList = new ArrayList<>(txs.stream().map(TX::toString).toList());
        String merkleRoot = MerkleRootUtil.createMerkleRoot(modifiableTxsList);
        MinerData mdata = new MinerData(previousHash, merkleRoot, timestamp, index);
        Header minedHeader = mine(mdata, DIFFICULTY);
        int size = BlockSizeCalculator.calculateBlockSize(minedHeader, txs);
        return new Header(minedHeader, size);
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

    private Key loadMinerJson() throws IOException {
        ClassPathResource resource = new ClassPathResource(minerFileName);
        Key data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
        log.info("Loaded miner JSON Data: " + data);
        return data;
    }

}
