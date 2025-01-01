package am.com.blockchain.wallet.controller;


import am.com.blockchain.wallet.service.user.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.wallet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/whoami")
    public ResponseEntity<?> getLoggedInUser() {
        try {
            UserDetailsImpl usr = (UserDetailsImpl) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(usr.getUsername());
        } catch (Exception e) {
            return ResponseEntity.ok("Not signed in");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userRepository.findAll());
    }

}
