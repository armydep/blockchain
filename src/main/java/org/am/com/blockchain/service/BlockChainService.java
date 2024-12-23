package org.am.com.blockchain.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.*;
import org.am.com.blockchain.model.wallet.Balance;
import org.am.com.blockchain.model.wallet.api.SendRequest;
import org.am.com.blockchain.repository.BlockChainRepository;
import org.am.com.blockchain.util.BtcOperation;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainService {

    private final BlockChainRepository blockChainRepository;
    private final List<MempoolTransaction> mempool = Collections.synchronizedList(new ArrayList<>());
    private final int FEE_SATOSHI = 5_000_000;


    public List<UTXO> getUTXO() {
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

    public CreateTxResponse submitTransaction(SendRequest sendRequest) {
        Optional<Balance> balanceOptional = findBalanceByAddress(sendRequest.getSender());
        if (balanceOptional.isEmpty()) {
            return CreateTxResponse.builder()
                    .submitted(false).message("Address does not contain any spendable inputs").build();
        }
        Balance balance = balanceOptional.get();
        double sum = BtcOperation.sumInts(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
        double remaining = BtcOperation.roundDoubleToBTC(balance.getAmount() - sum);
        if (balance.getAmount() >= sum) {
            long ts = System.currentTimeMillis() / 1000;
            Pair<List<UTXO>, Double> balancePair = getBalanceCoversSumForAddress(sendRequest.getSender(), sum);
            MempoolTransaction mpTx = new MempoolTransaction(sendRequest.getSender(),
                    sendRequest.getRecipient(), sum, ts, balancePair.getLeft(), balancePair.getRight());
            addTransaction(mpTx);
            String txid = "w_mp_tx_" + sendRequest.getSender() + "_" + ts;
            return CreateTxResponse.builder().txid(txid).submitted(true).totalToSend(sum).remaining(remaining).build();
        } else {
            return CreateTxResponse.builder()
                    .submitted(false).message("Not enough balance. Fee: 0." + FEE_SATOSHI + " btc").build();
        }
    }

    //todo: replace naive method by specific algorithm
    public Pair<List<UTXO>, Double> getBalanceCoversSumForAddress(String from, double sum) {
        Optional<Balance> optionalBalance = findBalanceByAddress(from);
        if (optionalBalance.isEmpty()) {
            return Pair.of(List.of(), 0d);
        }
        Balance balance = optionalBalance.get();
        List<UTXO> utxos = balance.getUTXOs();
        List<UTXO> result = new ArrayList<>();
        double currentSum = 0;
        for (UTXO utxo : utxos) {
            currentSum = BtcOperation.sumDoubles(utxo.getValue(), currentSum);
            result.add(utxo);
            if (currentSum >= sum) {
                break;
            }
        }
        return Pair.of(result, currentSum - sum);
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

    public Optional<Balance> findBalanceByAddress(@NotEmpty String address) {
        List<Balance> balances = getBalances();
        return balances.stream().filter(balance -> balance.getAddress().equals(address)).findFirst();
    }

    public List<MempoolTransaction> getMempool() {
        List<MempoolTransaction> copy = new ArrayList<>(mempool.size());
        for (MempoolTransaction item : mempool) {
            copy.add(item.clone());
        }
        return copy;
    }

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData, String txid) {
        UTXO utxo = new UTXO(txid, txOutEntry.getValue(), txOutEntry.getAddress(), txOutEntry.getN());
        utxoData.add(utxo);
    }

    private UTXO generateCoinBaseUTXO(CoinBaseEntry coinBaseEntry) {
        return new UTXO(coinBaseEntry.getTxid(),
                coinBaseEntry.getValue(), coinBaseEntry.getAddress(), coinBaseEntry.getN());
    }

    public void addTransaction(MempoolTransaction transactionRequest) {
        mempool.add(transactionRequest);
    }

    public List<MempoolTransaction> getBatch(int batchSize) {
        if (batchSize <= 0 || mempool.isEmpty()) {
            return List.of();
        }
        return mempool.subList(0, Math.min(batchSize, mempool.size()));
    }

    public void submitBlock(Block block) {
        blockChainRepository.addBlock(block);
    }

    public void clearMempoolTX(List<MempoolTransaction> validMempoolTransactions) {
        for (MempoolTransaction mempoolTransaction : validMempoolTransactions) {
            mempool.remove(mempoolTransaction);
        }
    }

    public void updateUTXO(List<MempoolTransaction> validMempoolTransactions) {
        log.warn("Not implemented");
    }

    public Block getLatestBlock() {
        return blockChainRepository.getLastBlock();
    }

    public List<Block> getBlocks() {
        return blockChainRepository.getBlocks();
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

}
