package org.am.com.blockchain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.blockchain.model.block.Block;
import org.am.com.blockchain.model.block.TX;
import org.am.com.blockchain.repository.BlockChainRepository;
import org.am.com.blockchain.util.BlockValidator;
import org.am.com.blockchain.util.TXValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainServiceV2 {

    private final BlockChainRepository blockChainRepository;
    private final List<TX> mempool = Collections.synchronizedList(new ArrayList<>());
    public static final int FEE_SATOSHI = 5_000_000;

    public CreateTxResponse submitToMempoolV2(TX tx) {
        TXValidator.validate(tx);
        mempool.add(tx);
        return CreateTxResponse.builder().txid("txid").submitted(true).build();
    }

    public List<TX> getMempoolBatch(int batchSize) {
        if (batchSize <= 0 || mempool.isEmpty()) {
            return List.of();
        }
        return mempool.subList(0, Math.min(batchSize, mempool.size()));
    }


    public void submitBlock(Block block) {
        BlockValidator.validate(block);
        blockChainRepository.addBlock(block);
        clearMempoolTX(block.getTx());
    }

    private void clearMempoolTX(List<TX> validMempoolTXs) {
        mempool.removeAll(validMempoolTXs);
    }

    public Block getLatestBlock() {
        return blockChainRepository.getLastBlock();
    }
}
