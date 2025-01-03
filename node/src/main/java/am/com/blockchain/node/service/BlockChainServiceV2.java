package am.com.blockchain.node.service;

import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.block.CoinBaseEntry;
import am.com.blockchain.common.exceptions.SignatureException;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TxInEntry;
import am.com.blockchain.common.tx.TxOutEntry;
import am.com.blockchain.common.util.TXValidator;
import am.com.blockchain.node.repository.BlockChainRepository;
import am.com.blockchain.node.repository.MempoolRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainServiceV2 {

    private final BlockChainRepository blockChainRepository;
    private final MempoolRepository mempoolRepository;

    public List<UTXO> getUTXO() {
        List<Block> blocks = getBlocks();
        List<UTXO> utxoData = new ArrayList<>();
        for (Block block : blocks) {
            CoinBaseEntry cbe = block.getCoinBaseEntry();
            if (cbe != null) {
                UTXO utxo = new UTXO(cbe.txid(), cbe.value(), cbe.address(), cbe.n());
                utxoData.add(utxo);
            }
            int i = cbe == null ? 0 : 1;
            for (; i < block.getTx().size(); i++) {
                TX tx = block.getTx().get(i);
                List<TxInEntry> txInEntries = tx.getVin();
                for (TxInEntry txInEntry : txInEntries) {
                    discardUTXOByTxIn(txInEntry, utxoData);
                }
                List<TxOutEntry> txOutEntries = tx.getVout();
                for (TxOutEntry txout : txOutEntries) {
                    UTXO utxo = new UTXO(tx.getTxid(), txout.getValue(), txout.getAddress(), txout.getN());
                    utxoData.add(utxo);
                }
            }
        }
        return utxoData;
    }

    public List<TX> getMempool() {
        return mempoolRepository.getMempool();
    }

    private void discardUTXOByTxIn(@NotNull TxInEntry txInEntry, List<UTXO> utxoData) {
        boolean removed = false;
        for (UTXO utxo : utxoData) {
            if (utxo.getTx().equals(txInEntry.getTxid()) && txInEntry.getVout().equals(utxo.getVout())) {
                utxoData.remove(utxo);
                removed = true;
                break;
            }
        }
        if (!removed) {
            log.warn("Not found UTXO for discard: " + txInEntry.getTxid());
        }
    }

    public Optional<Balance> findBalanceByAddress(@NotEmpty String address) {
        List<Balance> balances = getBalances();
        return balances.stream().filter(balance -> balance.getAddress().equals(address)).findFirst();
    }

    public List<Balance> getBalances() {
        Map<String, Balance> balancesMap = new HashMap<>();
        List<UTXO> utxos = getUTXO();
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
        return new ArrayList<>(balancesMap.values());
    }

    public CreateTxResponse submitToMempoolV2(TX tx) throws SignatureException {
        TXValidator.validate(tx);
        mempoolRepository.addTX(tx.copy());
        return CreateTxResponse.builder().submitted(true).build();
    }

    public List<TX> getMempoolBatch(int batchSize) {
        return mempoolRepository.getMempoolBatch(batchSize);
    }

    public void submitBlock(Block block) {
        blockChainRepository.addBlock(block);
    }

    public void clearMempoolTX(List<TX> validMempoolTXs) {
        mempoolRepository.clearMempoolTX(validMempoolTXs);
    }

    public Block getLatestBlock() {
        return blockChainRepository.getLastBlock();
    }

    public void clearTX(String txid) {
        mempoolRepository.clearTX(txid);
    }

    public List<Block> getBlocks() {
        return blockChainRepository.getBlocks();
    }
}
