package org.am.com.blockchainnode.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.BtcOperation;
import org.am.com.blockchainnode.GenesisLoadConfig;
import org.am.com.blockchainnode.MempoolService;
import org.am.com.blockchainnode.api.CreateTxResponse;
import org.am.com.blockchainnode.domain.block.*;
import org.am.com.blockchainnode.domain.wallet.Balance;
import org.am.com.blockchainnode.domain.wallet.api.SendRequest;
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

    public static final int FEE_SATOSHI = 1;
    //public static final float FEE = 0.1f;
    private final GenesisLoadConfig genesisLoadConfig;
    private final MempoolService mempoolService;

    public WalletController(GenesisLoadConfig genesisLoadConfig, MempoolService mempoolService) {
        this.genesisLoadConfig = genesisLoadConfig;
        this.mempoolService = mempoolService;
    }

    @GetMapping("/node")
    public List<Block> getBlocksTmp() {
        return genesisLoadConfig.getJsonData();
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Optional<Balance>> getBalance(@PathVariable String address) {
        Optional<Balance> balanceOptional = findBalanceByAddress(address);
        return ResponseEntity.ok(balanceOptional);
    }

    private Optional<Balance> findBalanceByAddress(String address) {
        List<Balance> balances = getBalances();
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

    //api - show commission fee
    //make tx mempool persist data into json
    //option to load mempool from json
    //should check that target address exists?
        /*
            1. is valid?
                -add from?
                -addr to?
                -amount?
                -is enough balance on sender address + fee commission?
             2. create TX
             3. put on tx mempool service
             4. lock utxo *****
         */
    @PostMapping("/send")
    public ResponseEntity<CreateTxResponse> send(@Valid @RequestBody SendRequest sendRequest) {
        if (!isValid(sendRequest)) {
            return ResponseEntity.badRequest().build();
        }
        CreateTxResponse response = createTXAndPutInMempool(sendRequest);
        return ResponseEntity.ok(response);
    }

    private CreateTxResponse createTXAndPutInMempool(@Valid SendRequest sendRequest) {
        Optional<Balance> balanceOptional = findBalanceByAddress(sendRequest.getFrom());
        CreateTxResponse createTxResponse = new CreateTxResponse();
        balanceOptional.ifPresent(balance -> {
            float sum = BtcOperation.sum(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
            float remaining = balance.getBalance() - sum;
            if (balance.getBalance() >= sum) {
                MempoolTransaction mempoolTransaction =
                        new MempoolTransaction(sendRequest.getFrom(),
                                sendRequest.getTo(), sum, System.nanoTime());
                createTxResponse.setTxid("" + mempoolService.addTransaction(mempoolTransaction));
                createTxResponse.setSubmitted(true);
                createTxResponse.setTotalToSend(sum);
                createTxResponse.setRemaining(remaining);
            } else {
                log.error("Not enough balance");
                createTxResponse.setSubmitted(false);
                createTxResponse.setMessage("Not enough balance");
            }
        });
        return createTxResponse;
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
}