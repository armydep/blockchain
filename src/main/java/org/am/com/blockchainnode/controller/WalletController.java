package org.am.com.blockchainnode.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.GenesisLoadConfig;
import org.am.com.blockchainnode.domain.block.*;
import org.am.com.blockchainnode.domain.wallet.Balance;
import org.am.com.blockchainnode.domain.wallet.api.SendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/wallet/api")
public class WalletController {

    @Autowired
    private GenesisLoadConfig genesisLoadConfig;

    @GetMapping("/node")
    public List<Block> getBlocksTmp() {
        return genesisLoadConfig.getJsonData();
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Optional<Balance>> getBalance(@PathVariable String address) {
        List<Balance> balances = getBalances();
        Optional<Balance> balanceOptional = findBalanceByAddress(balances, address);
        return ResponseEntity.ok(balanceOptional);
    }

    private Optional<Balance> findBalanceByAddress(List<Balance> balances, String address) {
        return balances.stream()
                .filter(balance -> balance.getAddress().equals(address))
                .findFirst();
    }

    @GetMapping("/balance")
    public ResponseEntity<List<Balance>> getAllBalances() {
        List<Balance> list = getBalances();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private List<Balance> getBalances() {
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

    private boolean isValid(SendRequest sendRequest) {
        return sendRequest != null;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody SendRequest sendRequest) {
        if (!isValid(sendRequest)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/utxo")
    public List<UTXO> getUTXO() {
        List<Block> blocks = genesisLoadConfig.getJsonData();
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

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData, String txid) {
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
}