package org.am.com.blockchain.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.api.CreateTxResponse;
import org.am.com.blockchain.model.wallet.Balance;
import org.am.com.blockchain.model.wallet.api.SendRequest;
import org.am.com.blockchain.service.BlockChainService;
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

    public WalletController(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<?> getBalance(@PathVariable String address) {
        return blockChainService
                .findBalanceByAddress(address)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/balance")
    public ResponseEntity<List<Balance>> getAllBalances() {
        return ResponseEntity.ok(blockChainService.getBalances());
    }

    @PostMapping("/send")
    public ResponseEntity<CreateTxResponse> send(@Valid @RequestBody SendRequest sendRequest) {
        CreateTxResponse response = blockChainService.submitTransaction(sendRequest);
        if (response.isSubmitted()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}