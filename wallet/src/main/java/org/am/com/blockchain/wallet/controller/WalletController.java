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
}