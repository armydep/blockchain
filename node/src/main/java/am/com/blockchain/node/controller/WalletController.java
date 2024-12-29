package am.com.blockchain.node.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.node.model.wallet.api.SendRequest;
import am.com.blockchain.node.service.BlockChainService;
import am.com.blockchain.node.service.BlockChainServiceV2;
import am.com.blockchain.node.service.UsersService;
import am.com.blockchain.common.exceptions.SignatureException;
import am.com.blockchain.common.util.crypto.BitcoinAddressValidator;
import am.com.blockchain.common.tx.TX;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> sendV2(/*@Valid*/ @RequestBody TX sendRequest) {
        try {
            CreateTxResponse response = blockChainServiceV2.submitToMempoolV2(sendRequest);
            if (response.getSubmitted()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (SignatureException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
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