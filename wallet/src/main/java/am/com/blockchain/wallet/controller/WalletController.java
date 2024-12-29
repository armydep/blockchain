package am.com.blockchain.wallet.controller;

//import io.jsonwebtoken.Jwt;

import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.wallet.controller.api.WalletSend;
import am.com.blockchain.wallet.service.tx.TxService;
import am.com.blockchain.wallet.service.user.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class WalletController {
    private final TxService txService;

    public WalletController(TxService txService) {
        this.txService = txService;
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<Balance> getBalance(@PathVariable String address) {
        UserDetailsImpl usr = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Balance balance = txService.getBalance(address, usr.getUsername());
        return ResponseEntity.ok(balance);
//        return blockChainService
//                .findBalanceByAddress(address).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody WalletSend send) throws IOException {
        UserDetailsImpl usr = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Usr: " + usr);
        //send.setUsername(usr.getUsername());
        try {
            CreateTxResponse response = txService.send(send, usr.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
//jwt.get
//"Token subject: " + jwt.getSubject();
//        String fromAddress = send.getSender();
//        if (belongToUser(fromAddress)) {
//
//        }
//        return ResponseEntity.ok(txService.send(send));

    /*
        *** logged in *
        show my balance
     */
/*
    @GetMapping("/balance/{address}")
    public ResponseEntity<Balance> getBalance(@PathVariable String address) throws IOException {
        Balance response = restClient
                .sendGetRequest("http://localhost:8081/api/balance", address, Balance.class);
        return ResponseEntity.ok(response);
    }
*/
/*
        Balance response = restClient
                .sendGetRequest("http://localhost:8081/api/balance", address, Balance.class);
        return ResponseEntity.ok(response);
*/
