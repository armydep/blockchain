package org.am.com.blockchain.wallet.controller;


import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.wallet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/wallet/home")
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userRepository.findAll());
    }

}
