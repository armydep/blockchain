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

    public static final int FEE_SATOSHI = 1;
    //public static final float FEE = 0.1f;
    private final BlockChainService blockChainService;

    public WalletController(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    @GetMapping("/node")
    public List<Block> getBlocksTmp() {
        return blockChainService.getBlocks();
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Optional<Balance>> getBalance(@PathVariable String address) {
        Optional<Balance> balanceOptional = findBalanceByAddress(address);
        return ResponseEntity.ok(balanceOptional);
    }

    private Optional<Balance> findBalanceByAddress(String address) {
        List<Balance> balances = blockChainService.getBalances();
        return balances.stream()
                .filter(balance -> balance.getAddress().equals(address))
                .findFirst();
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
        Optional<Balance> balanceOptional = findBalanceByAddress(sendRequest.getFrom());
        CreateTxResponse createTxResponse = new CreateTxResponse();
        balanceOptional.ifPresent(balance -> {
            float sum = BtcOperation.sum(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
            float remaining = balance.getBalance() - sum;
            if (balance.getBalance() >= sum) {
                MempoolTransaction mempoolTransaction =
                        new MempoolTransaction(sendRequest.getFrom(),
                                sendRequest.getTo(), sum, System.nanoTime());
                createTxResponse.setTxid("" + blockChainService.addTransaction(mempoolTransaction));
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
        return blockChainService.getUTXO();
    }
}