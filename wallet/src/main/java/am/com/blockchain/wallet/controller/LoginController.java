package am.com.blockchain.wallet.controller;

import am.com.blockchain.common.user.Key;
import am.com.blockchain.common.util.crypto.CryptoUtil;
import am.com.blockchain.wallet.config.jwt.JwtUtils;
import am.com.blockchain.wallet.model.User;
import am.com.blockchain.wallet.model.login.JwtResponse;
import am.com.blockchain.wallet.repository.UserRepository;
import am.com.blockchain.wallet.service.user.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User signUpRequest,
                                          HttpServletRequest request) throws Exception {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Error: Username is already taken!");
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        generateAddress(user);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    private void generateAddress(User user) throws Exception {
        Key key = new Key();
        CryptoUtil.generateKeys(key);
        user.setAddress(key.getAddress());
        user.setPublicKey(key.getPublicKey());
        user.setPrivateKey(key.getPrivateKey());
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestParam String username,
                                              @RequestParam String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
    }

}