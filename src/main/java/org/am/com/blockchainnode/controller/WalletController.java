package org.am.com.blockchainnode.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.GenesisLoadConfig;
import org.am.com.blockchainnode.domain.block.*;
import org.am.com.blockchainnode.domain.TransactionRequest;
import org.am.com.blockchainnode.domain.wallet.Balance;
import org.am.com.blockchainnode.domain.wallet.api.SendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/wallet/api")
public class WalletController {

    @Autowired
    private GenesisLoadConfig genesisLoadConfig;

    @GetMapping("/node")
    public List<Block> getBlocksTmp() {
        return genesisLoadConfig.getJsonData();//getBlocks();
    }

    private static ArrayList<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();

        Block block1 = new Block();
        block1.setIndex(1L);
        block1.setHash("123456");
        block1.setPreviousHash("000000");
        block1.setData("Block 1 data: " + new Date());

        TxInEntry txInEntry = new TxInEntry("t11", 0, null);
        TxOutEntry txOutEntry = new TxOutEntry(2, "ad123", 4);
        List<TxInEntry> txInEntries = new ArrayList<>();
        txInEntries.add(txInEntry);
        List<TxOutEntry> txOutEntries = new ArrayList<>();
        txOutEntries.add(txOutEntry);
        TX tx = new TX("t10000", txInEntries, txOutEntries);
        List<TX> txList = new ArrayList<>();
        txList.add(tx);
        block1.setTx(txList);

        Block block2 = new Block();
        block2.setIndex(2L);
        block2.setHash("789012");
        block2.setPreviousHash("123456");
        block2.setData("Block 2 data: " + new Date());

        blocks.add(block1);
        blocks.add(block2);

        return blocks;
    }

    @GetMapping("/tx")
    public void getTransactionStatus(TransactionRequest transactionRequest) {
        //mempoolService.addTransaction(transactionRequest);
    }

    @GetMapping("/balance/{address}")
    public Balance getBalance(@PathVariable String address) {
        return new Balance(3, 100, address);
    }

    private boolean isValid(SendRequest sendRequest) {
        return true;
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
        List<Block> blocks = genesisLoadConfig.getJsonData();//getBlocks();
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