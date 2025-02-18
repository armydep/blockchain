package am.com.blockchain.wallet.controller;


import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.wallet.api.WalletSend;
import am.com.blockchain.wallet.model.User;
import am.com.blockchain.wallet.repository.UserRepository;
import am.com.blockchain.wallet.service.tx.TxService;
import am.com.blockchain.wallet.service.user.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/wallet/api/operation")
public class WalletController {
    private final UserRepository userRepository;
    private final TxService txService;

    public WalletController(UserRepository userRepository, TxService txService) {
        this.userRepository = userRepository;
        this.txService = txService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String address = getUserAddress(username);
        if (address == null) {
            return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
        Balance balance = txService.getBalance(address/*, usr.getUsername()*/);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/balance/all")
    public ResponseEntity<Balance> getAllBalances() {
        //return all wallets users addresses
        // or
        // all balances from node by calling node balance api
        return ResponseEntity.noContent().build();
    }

    //deprecate
    @GetMapping("/balance/v1/{address}")
    public ResponseEntity<Balance> getBalanceByAddress(@PathVariable String address) {
        Balance balance = txService.getBalance(address/*, usr.getUsername()*/);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody WalletSend send) throws IOException {
        UserDetailsImpl usr =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Usr: " + usr);
        try {
            if (!belongToUser(usr.getUsername(), send.getSender())) {
                throw new NoSuchElementException("Address not owned by user");
            }
            CreateTxResponse response = txService.send(send, usr.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getUserAddress(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getAddress).orElse(null);
    }

    private boolean belongToUser(String username, String address) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && address.equals(user.get().getAddress());
    }
}
