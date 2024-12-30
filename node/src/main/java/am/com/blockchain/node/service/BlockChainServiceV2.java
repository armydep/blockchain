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
import am.com.blockchain.common.util.BlockValidator;
import am.com.blockchain.common.util.TXValidator;
import am.com.blockchain.node.repository.BlockChainRepository;
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
    private final List<TX> mempool = Collections.synchronizedList(new ArrayList<>());
    public static final int FEE_SATOSHI = 5_000_000;

    public List<UTXO> getUTXO() {
        List<Block> blocks = getBlocks();
        List<UTXO> utxoData = new ArrayList<>();
        for (Block block : blocks) {
            CoinBaseEntry coinBaseEntry = block.getCoinBaseEntry();
            if (coinBaseEntry != null) {
                UTXO utxo = generateCoinBaseUTXO(coinBaseEntry);
                utxoData.add(utxo);
            }
            int i = coinBaseEntry == null ? 0 : 1;
            for (; i < block.getTx().size(); i++) {
                TX tx = block.getTx().get(i);
                List<TxInEntry> txInEntries = tx.getVin();
                for (TxInEntry txInEntry : txInEntries) {
                    discardUTXOByTxIn(txInEntry, utxoData);
                }
                List<TxOutEntry> txOutEntries = tx.getVout();
                for (TxOutEntry txOutEntry : txOutEntries) {
                    generateAndInsertUTXOByTxOut(txOutEntry, utxoData, tx.getTxid());
                }
            }
        }
        return utxoData;
    }

    public List<TX> getMempool() {
        List<TX> copy = new ArrayList<>(mempool.size());
        for (TX item : mempool) {
            copy.add(item.copy());
        }
        return copy;
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

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData, String txid) {
        UTXO utxo = new UTXO(txid, txOutEntry.getValue(), txOutEntry.getAddress(), txOutEntry.getN());
        utxoData.add(utxo);
    }

    private UTXO generateCoinBaseUTXO(CoinBaseEntry coinBaseEntry) {
        return new UTXO(coinBaseEntry.getTxid(),
                coinBaseEntry.getValue(), coinBaseEntry.getAddress(), coinBaseEntry.getN());
    }

    public Optional<Balance> findBalanceByAddress(@NotEmpty String address) {
        List<Balance> balances = getBalances();
        return balances.stream().filter(balance -> balance.getAddress().equals(address)).findFirst();
    }

    public List<Balance> getBalances() {
        Map<String, Balance> balancesMap = new HashMap<>();
        List<Balance> list = new ArrayList<>(List.of());
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

    public List<Block> getBlocks() {
        return blockChainRepository.getBlocks();
    }
}
