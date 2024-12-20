package org.am.com.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.*;
import org.am.com.blockchain.model.wallet.Balance;
import org.am.com.blockchain.model.wallet.api.SendRequest;
import org.am.com.blockchain.util.BtcOperation;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainService {
    private List<Block> blocks;
    private final ObjectMapper objectMapper;
    private static final List<MempoolTransaction> mempool =
            Collections.synchronizedList(new ArrayList<>());
    private final Object lock = new Object();
    private final int FEE_SATOSHI = 5;

    @PostConstruct
    public void init() throws IOException {
        synchronized (lock) {
            List<Block> oblocks = loadBlocks();
            blocks = new InsertionOnlyList<>();
            blocks.addAll(oblocks);
        }
    }

    private List<Block> loadBlocks() throws IOException {
        ClassPathResource resource = new ClassPathResource("genesis.json");
        List<Block> data = objectMapper.readValue(resource.getInputStream(),
                new TypeReference<>() {
                });
        log.info("Loaded JSON Data: " + data);
        return data;
    }

    public List<Block> getBlocks() {
        synchronized (lock) {
            return blocks;
        }
    }

    public List<UTXO> getUTXO() {
        synchronized (lock) {
            List<Block> blocks = getBlocks();
            List<UTXO> utxoData = new ArrayList<>();
            for (Block block : blocks) {
                CoinBaseEntry coinBaseEntry = block.getCoinBaseEntry();
                if (coinBaseEntry != null) {
                    UTXO utxo = generateCoinBaseUTXO(coinBaseEntry);
                    if (utxo != null) {
                        utxoData.add(utxo);
                    }
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
    }

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData,
                                              String txid) {
        UTXO utxo = new UTXO(txid, txOutEntry.getValue(), txOutEntry.getAddress(),
                txOutEntry.getN());
        utxoData.add(utxo);
    }

    private void discardUTXOByTxIn(TxInEntry txInEntry, List<UTXO> utxoData) {
        boolean removed = false;
        for (UTXO utxo : utxoData) {
            if (utxo.getTx().equals(txInEntry.getTxid()) && utxo.getVout() == txInEntry.getVout()) {
                utxoData.remove(utxo);
                removed = true;
                break;
            }
        }
        if (!removed) {
            log.warn("Not found UTXO for discard: " + txInEntry.getTxid());
        }
    }

    private UTXO generateCoinBaseUTXO(CoinBaseEntry coinBaseEntry) {
        return new UTXO(coinBaseEntry.getTxid(), coinBaseEntry.getValue(),
                coinBaseEntry.getAddress(), coinBaseEntry.getN());
    }

    public List<Balance> getBalances() {
        synchronized (lock) {
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
    }

    public Optional<Balance> findBalanceByAddress(String address) {
        synchronized (lock) {
            List<Balance> balances = getBalances();
            return balances.stream()
                    .filter(balance -> balance.getAddress().equals(address))
                    .findFirst();
        }
    }

    public void addTransaction(MempoolTransaction transactionRequest) {
        synchronized (lock) {
            mempool.add(transactionRequest);
        }
    }

    public List<MempoolTransaction> getMempool() {
        synchronized (lock) {
            List<MempoolTransaction> copy = new ArrayList<>(mempool.size());
            for (MempoolTransaction item : mempool) {
                copy.add(item.clone());
            }
            return copy;
        }
    }

    public List<MempoolTransaction> getBatch(int batchSize) {
        synchronized (lock) {
            if (batchSize <= 0 || mempool.isEmpty()) {
                return List.of();
            }
            return mempool.subList(0, Math.min(batchSize, mempool.size()));
        }
    }

    public void submitBlock(Block block) {
        synchronized (lock) {
            blocks.add(block);
        }
    }

    public void clearMempoolTX(List<MempoolTransaction> validMempoolTransactions) {
        synchronized (lock) {
            for (MempoolTransaction mempoolTransaction : validMempoolTransactions) {
                mempool.remove(mempoolTransaction);
            }
        }
    }

    public void updateUTXO(List<MempoolTransaction> validMempoolTransactions) {
        log.warn("Not implemented yet!");
    }

    public Block getLatestBlock() {
        return blocks.getLast();
    }

    public CreateTxResponse submitTransaction(SendRequest sendRequest) {
        Optional<Balance> balanceOptional = findBalanceByAddress(sendRequest.getFrom());
        if (balanceOptional.isEmpty()) {
            return CreateTxResponse.builder()
                    .submitted(false)
                    .message("No balance for sending tx")
                    .build();
        }
        Balance balance = balanceOptional.get();
        float sum = BtcOperation.sum(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
        float remaining = balance.getAmount() - sum;
        if (balance.getAmount() >= sum) {
            long ts = System.currentTimeMillis() / 1000;
            Pair<List<UTXO>, Float> balancePair =
                    getBalanceCoversSumForAddress(sendRequest.getFrom(), sum);
            MempoolTransaction mpTx = new MempoolTransaction(sendRequest.getFrom(),
                    sendRequest.getTo(), sum, ts, balancePair.getLeft(),
                    balancePair.getRight());
            addTransaction(mpTx);
            String txid = "w_mp_tx_" + sendRequest.getFrom() + "_" + ts;
            return CreateTxResponse.builder()
                    .txid(txid)
                    .submitted(true)
                    .totalToSend(sum)
                    .remaining(remaining)
                    .build();
        } else {
            return CreateTxResponse
                    .builder()
                    .submitted(false)
                    .message("Not enough balance. Fee: 0." + FEE_SATOSHI + " btc")
                    .build();
        }

    }

    //todo: replace naive method by specific algorithm
    public Pair<List<UTXO>, Float> getBalanceCoversSumForAddress(String from, float sum) {
        synchronized (lock) {
            Optional<Balance> optionalBalance = findBalanceByAddress(from);
            if (optionalBalance.isEmpty()) {
                return Pair.of(List.of(), 0f);
            }
            Balance balance = optionalBalance.get();
            List<UTXO> utxos = balance.getUtxos();
            List<UTXO> result = new ArrayList<>();
            float currentSum = 0;
            for (UTXO utxo : utxos) {
                currentSum = (float) BtcOperation.sumFloats((double) utxo.getValue(),
                        (double) currentSum);
                result.add(utxo);
                if (currentSum >= sum) {
                    break;
                }
            }
            return Pair.of(result, currentSum - sum);
        }
    }
}
