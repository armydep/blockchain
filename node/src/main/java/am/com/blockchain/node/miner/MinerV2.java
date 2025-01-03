package am.com.blockchain.node.miner;

import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.exceptions.MissingFeeException;
import am.com.blockchain.common.tx.TxInEntry;
import am.com.blockchain.common.tx.TxOutEntry;
import am.com.blockchain.common.util.BlockValidator;
import am.com.blockchain.common.util.BtcOperation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.block.Header;
import am.com.blockchain.common.user.Key;
import am.com.blockchain.node.service.BlockChainServiceV2;
import am.com.blockchain.common.util.BlockSizeCalculator;
import am.com.blockchain.node.util.MerkleRootUtil;
import am.com.blockchain.common.util.crypto.CryptoUtil;
import am.com.blockchain.common.tx.TX;
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
    private final double COINBASE = 5;
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
            try {
                List<TX> orig = TX.copyList(mempoolTXs);
                Block block = assemblyBlock(mempoolTXs);
                BlockValidator.validate(block);
                blockChainService.submitBlock(block);
                blockChainService.clearMempoolTX(orig);
            } catch (MissingFeeException e) {
                log.warn("Invalid TX. Missing fee - " + e.getMessage());
                blockChainService.clearTX(e.getTxid());
            }
        } else {
            log.info("No valid mempool transactions");
        }
        log.info("Mining finished., count: {}", currentCount);
    }

    private Block assemblyBlock(List<TX> txso) throws MissingFeeException {
        String cbtxid = "miner_v2_set_cb_txid_" + count.get();
        double fee = calculateFees(txso);
        double reward = BtcOperation.sumDoubles(COINBASE, fee);
        TX coinbase = TX.generateCoinBaseTX(cbtxid, key.getAddress(), reward);
        txso.addFirst(coinbase);
        Header header = mineAndCreateHeader(TX.copyList(txso));
        return new Block(header, txso);
    }

    private double calculateFees(List<TX> txso) throws MissingFeeException {
        double fee = 0;
        for (TX tx : txso) {
            fee = fee + calculateFee(tx);
        }
        return fee;
    }

    private double calculateFee(TX tx) throws MissingFeeException {
        double in = 0;
        double out = 0;
        for (TxInEntry ine : tx.getVin()) {
            UTXO utxo = findUtxoByTxid(ine.getTxid(), ine.getVout());
            in = in + utxo.getValue();
        }
        for (TxOutEntry oute : tx.getVout()) {
            out = out + oute.getValue();
        }
        double fee = in - out;
        if (fee <= 0) {
            throw new MissingFeeException(tx.getTxid(), fee);
        }
        return fee;
    }

    private UTXO findUtxoByTxid(String txid, Integer vout) {
        List<UTXO> utxos = blockChainService.getUTXO();
        for (UTXO u : utxos) {
            if (u.getTx().equals(txid) && u.getVout().equals(vout)) {
                return u;
            }
        }
        return null;
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
