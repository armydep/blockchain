package org.am.com.blockchain.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.balance.Balance;
import org.am.com.blockchain.model.wallet.api.SendRequest;
import org.am.com.blockchain.service.BlockChainService;
import org.am.com.blockchain.service.BlockChainServiceV2;
import org.am.com.blockchain.service.UsersService;
import org.am.com.util.crypto.BitcoinAddressValidator;
import org.am.com.tx.TX;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class WalletController {
    private final BlockChainService blockChainService;
    private final BlockChainServiceV2 blockChainServiceV2;
    private final UsersService usersService;

    public WalletController(BlockChainService blockChainService,
                            BlockChainServiceV2 blockChainServiceV2,
                            UsersService usersService) {
        this.blockChainService = blockChainService;
        this.blockChainServiceV2 = blockChainServiceV2;
        this.usersService = usersService;
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<?> getBalance(@PathVariable String address) {
        return blockChainService
                .findBalanceByAddress(address).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/balance")
    public ResponseEntity<List<Balance>> getAllBalances() {
        return ResponseEntity.ok(blockChainService.getBalances());
    }

    @PostMapping("/v2/send")
    public ResponseEntity<CreateTxResponse> sendV2(/*@Valid*/ @RequestBody TX sendRequest) {
        CreateTxResponse response = blockChainServiceV2.submitToMempoolV2(sendRequest);
        if (response.getSubmitted()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<CreateTxResponse> send(@Valid @RequestBody SendRequest sendRequest) {
        if (!isValid(sendRequest)) {
            String msg = "One of addresses is not valid";
            return ResponseEntity.badRequest().body(CreateTxResponse.builder().submitted(false).message(msg).build());
        }
        CreateTxResponse response = blockChainService.submitTransaction(sendRequest);
        if (response.getSubmitted()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    private boolean isValid(SendRequest sendRequest) {
        if (!usersService.isExist(sendRequest.getSender())) {
            log.info("User with address {} is not exist", sendRequest.getSender());
            return false;
        }
        if (!BitcoinAddressValidator.validateBitcoinAddress(sendRequest.getRecipient())) {
            log.info("Recipients address format invalid {}", sendRequest.getRecipient());
            return false;
        }
        if (!BitcoinAddressValidator.validateBitcoinAddress(sendRequest.getSender())) {
            log.info("Sender address format invalid {}", sendRequest.getSender());
            return false;
        }
        return true;
    }
}