package org.am.com.blockchain.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.blockchain.model.wallet.Balance;
import org.am.com.blockchain.model.wallet.api.SendRequest;
import org.am.com.blockchain.service.BlockChainService;
import org.am.com.blockchain.service.UsersService;
import org.am.com.blockchain.util.crypto.BitcoinAddressValidator;
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
    private final UsersService usersService;

    public WalletController(BlockChainService blockChainService, UsersService usersService) {
        this.blockChainService = blockChainService;
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