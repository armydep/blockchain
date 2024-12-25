package org.am.com.blockchain.wallet.controller;

import lombok.extern.slf4j.Slf4j;
import org.am.com.balance.Balance;
import org.am.com.blockchain.wallet.rest.RestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class WalletController {
//    private final BlockChainService blockChainService;
//    private final BlockChainServiceV2 blockChainServiceV2;
//    private final UsersService usersService;

//    public WalletController(BlockChainService blockChainService,
//                            BlockChainServiceV2 blockChainServiceV2,
//                            UsersService usersService) {
//        this.blockChainService = blockChainService;
//        this.blockChainServiceV2 = blockChainServiceV2;
//        this.usersService = usersService;
//    }

    private final RestClient restClient;

    public WalletController(RestClient restClient) {
        this.restClient = restClient;
    }



    /*
        *** logged in *
        show my balance

     */
    @GetMapping("/balance/{address}")
    public ResponseEntity<Balance> getBalance(@PathVariable String address) throws IOException {
        Balance response = restClient
                .sendGetRequest("http://localhost:8081/api/balance", address, Balance.class);
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/balance")
//    public ResponseEntity<List<Balance>> getAllBalances() {
//        return ResponseEntity.ok(blockChainService.getBalances());
//    }
//
//    @PostMapping("/v2/send")
//    public ResponseEntity<CreateTxResponse> sendV2(/*@Valid*/ @RequestBody TX sendRequest) {
//        CreateTxResponse response = blockChainServiceV2.submitToMempoolV2(sendRequest);
//        if (response.getSubmitted()) {
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<CreateTxResponse> send(@Valid @RequestBody SendRequest sendRequest) {
//        if (!isValid(sendRequest)) {
//            String msg = "One of addresses is not valid";
//            return ResponseEntity.badRequest().body(CreateTxResponse.builder().submitted(false)
//            .message(msg).build());
//        }
//        CreateTxResponse response = blockChainService.submitTransaction(sendRequest);
//        if (response.getSubmitted()) {
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    private boolean isValid(SendRequest sendRequest) {
//        if (!usersService.isExist(sendRequest.getSender())) {
//            log.info("User with address {} is not exist", sendRequest.getSender());
//            return false;
//        }
//        if (!BitcoinAddressValidator.validateBitcoinAddress(sendRequest.getRecipient())) {
//            log.info("Recipients address format invalid {}", sendRequest.getRecipient());
//            return false;
//        }
//        if (!BitcoinAddressValidator.validateBitcoinAddress(sendRequest.getSender())) {
//            log.info("Sender address format invalid {}", sendRequest.getSender());
//            return false;
//        }
//        return true;
//    }
}