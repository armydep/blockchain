package org.am.com.blockchainnode.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.domain.Block;
import org.am.com.blockchainnode.domain.TransactionRequest;
import org.am.com.blockchainnode.domain.wallet.Balance;
import org.am.com.blockchainnode.domain.wallet.api.SendRequest;
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

    @GetMapping("/node")
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();

        Block block1 = new Block();
        block1.setIndex(1L);
        block1.setHash("123456");
        block1.setPreviousHash("000000");
        block1.setData("Block 1 data: " + new Date());

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
}