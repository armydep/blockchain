package org.am.com.blockchainnode.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.api.CreateTxResponse;
import org.am.com.blockchainnode.model.MempoolTransaction;
import org.am.com.blockchainnode.model.block.Block;
import org.am.com.blockchainnode.model.block.UTXO;
import org.am.com.blockchainnode.model.wallet.Balance;
import org.am.com.blockchainnode.model.wallet.api.SendRequest;
import org.am.com.blockchainnode.service.BlockChainService;
import org.am.com.blockchainnode.util.BtcOperation;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/wallet/api")
public class WalletController {

    public static final int FEE_SATOSHI = 5;
    //public static final float FEE = 0.1f;
    private final BlockChainService blockChainService;

    public WalletController(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Optional<Balance>> getBalance(@PathVariable String address) {
        Optional<Balance> balanceOptional = blockChainService.findBalanceByAddress(address);
        return ResponseEntity.ok(balanceOptional);
    }

    @GetMapping("/balance")
    public ResponseEntity<List<Balance>> getAllBalances() {
        List<Balance> list = blockChainService.getBalances();
        return new ResponseEntity<>(list, HttpStatus.OK);
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
        Optional<Balance> balanceOptional =
                blockChainService.findBalanceByAddress(sendRequest.getFrom());
        CreateTxResponse createTxResponse = new CreateTxResponse();
        if (balanceOptional.isPresent()) {
            Balance balance = balanceOptional.get();
            float sum = BtcOperation.sum(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
            float remaining = balance.getAmount() - sum;
            if (balance.getAmount() >= sum) {

                long ts = System.currentTimeMillis() / 1000;
                Pair<List<UTXO>, Float> balancePair = blockChainService
                        .getBalanceCoversSumForAddress(sendRequest.getFrom(), sum);
                MempoolTransaction mpTx = new MempoolTransaction(sendRequest.getFrom(),
                        sendRequest.getTo(), sum, ts, balancePair.getLeft(),
                        balancePair.getRight());
                //if (remaining > 0) {
                    /*MempoolTransaction changeTx = new MempoolTransaction(
                            sendRequest.getFrom(), sendRequest.getFrom(), remaining, ts);
                    blockChainService.addTransaction(changeTx);*/
                //  mpTx.addChangeTxOut(remaining);
                //}
                blockChainService.addTransaction(mpTx);
                String txid = "w_mp_tx_" + sendRequest.getFrom() + "_" + ts;
                createTxResponse.setTxid(txid);
                createTxResponse.setSubmitted(true);
                createTxResponse.setTotalToSend(sum);
                createTxResponse.setRemaining(remaining);
            } else {
                log.info("Not enough balance for sending tx {}", sendRequest);
                createTxResponse.setSubmitted(false);
                createTxResponse.setMessage("Not enough balance. Fee: 0." + FEE_SATOSHI + " btc");
            }
        } else {
            log.info("No balance for sending tx {}", sendRequest);
            createTxResponse.setSubmitted(false);
            createTxResponse.setMessage("No balance for sending tx");
        }
        return createTxResponse;
    }

    @GetMapping("/utxo")
    public List<UTXO> getUTXO() {
        return blockChainService.getUTXO();
    }
}