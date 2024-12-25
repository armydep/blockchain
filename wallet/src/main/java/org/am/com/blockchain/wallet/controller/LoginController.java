package org.am.com.blockchain.wallet.controller;

import lombok.extern.slf4j.Slf4j;
import org.am.com.balance.Balance;
import org.am.com.blockchain.wallet.rest.RestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class LoginController {


    @GetMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok("Okk tx");
    }

    @GetMapping("/register")
    public ResponseEntity<?> register() {
        return ResponseEntity.ok("Okk tx");
    }


    @GetMapping("/test")
    public ResponseEntity<?> testAuth() {
        return ResponseEntity.ok("authed");
    }
}