package org.am.com.blockchain.wallet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.wallet.config.jwt.JwtUtils;
import org.am.com.blockchain.wallet.model.User;
import org.am.com.blockchain.wallet.model.login.JwtResponse;
import org.am.com.blockchain.wallet.model.login.LoginRequest;
import org.am.com.blockchain.wallet.repository.UserRepository;
import org.am.com.blockchain.wallet.service.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User signUpRequest,
                                          HttpServletRequest request)
            throws UnsupportedEncodingException {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Error: Username is already taken!");
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));

    }

    @RequestMapping("/user-dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard() {
        return "My Dashboard";
    }


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