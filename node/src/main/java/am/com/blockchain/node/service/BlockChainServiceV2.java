package am.com.blockchain.node.service;

import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.node.repository.BlockChainRepository;
import am.com.blockchain.common.exceptions.SignatureException;
import am.com.blockchain.common.util.BlockValidator;
import am.com.blockchain.common.util.TXValidator;
import am.com.blockchain.common.tx.TX;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainServiceV2 {

    private final BlockChainRepository blockChainRepository;
    private final List<TX> mempool = Collections.synchronizedList(new ArrayList<>());
    public static final int FEE_SATOSHI = 5_000_000;

    public Optional<Balance> findBalanceByAddress(@NotEmpty String address) {
        List<Balance> balances = getBalances();
        return balances.stream().filter(balance -> balance.getAddress().equals(address)).findFirst();
    }

    public List<Balance> getBalances() {
        Map<String, Balance> balancesMap = new HashMap<>();
        List<Balance> list = new ArrayList<>(List.of());
        List<UTXO> utxos = blockChainRepository.getUTXO();
        for (UTXO utxo : utxos) {
            String address = utxo.getAddress();
            Balance balance;
            if (balancesMap.containsKey(address)) {
                balance = balancesMap.get(address);
                balance.addUTXO(utxo);
            } else {
                balance = new Balance(utxo);
                balancesMap.put(address, balance);
            }
        }
        list.addAll(balancesMap.values());
        return list;
    }

    public CreateTxResponse submitToMempoolV2(TX tx) throws SignatureException {
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

    public List<UTXO> getUTXO() {
        return blockChainRepository.getUTXO();
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

    public void clearTX(String txid) {
        TX tx = null;
        for (TX t : mempool) {
            if (t.getTxid().equals(txid)) {
                tx = t;
                break;
            }
        }
        mempool.remove(tx);
    }
}
