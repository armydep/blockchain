package org.am.com.blockchainnode.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.model.block.*;
import org.am.com.blockchainnode.model.wallet.Balance;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainService {
    //should be deep cloned in getter
    @Getter
    private List<Block> blocks;
    private final ReentrantLock lock = new ReentrantLock();
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        blocks = loadBlocks();
    }

    private List<Block> loadBlocks() {
        try {
            ClassPathResource resource = new ClassPathResource("genesis.json");
            List<Block> data = objectMapper.readValue(resource.getInputStream(),
                    new TypeReference<>() {
                    });
            log.info("Loaded JSON Data: " + data);
            return data;
        } catch (Throwable e) {
            log.error("Failed to load JSON: " + e.getMessage());
        }
        return List.of();
    }

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
            for (TX tx : block.getTx()) {
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

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData,
                                              String txid) {
        UTXO utxo = UTXO.builder().
                value(txOutEntry.getValue())
                .address(txOutEntry.getAddress())
                .vout(txOutEntry.getN())
                .tx(txid)
                .build();
        utxoData.add(utxo);
    }

    private void discardUTXOByTxIn(TxInEntry txInEntry, List<UTXO> utxoData) {
        for (UTXO utxo : utxoData) {
            if (utxo.getTx().equals(txInEntry.getTxid()) && utxo.getVout() == txInEntry.getVout()) {
                utxoData.remove(utxo);
                break;
            }
        }
    }

    private UTXO generateCoinBaseUTXO(CoinBaseEntry coinBaseEntry) {
        return UTXO.builder().
                value(coinBaseEntry.getValue())
                .address(coinBaseEntry.getAddress())
                .vout(coinBaseEntry.getN())
                .tx(coinBaseEntry.getTxid())
                .build();
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
                balance.setBalance(balance.getBalance() + utxo.getValue());
            } else {
                balance = new Balance(utxo);
                balancesMap.put(address, balance);
            }
        }
        list.addAll(balancesMap.values());
        return list;
    }
}
