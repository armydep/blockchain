package org.am.com.blockchain.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.am.com.blockchain.model.user.User;
import org.am.com.blockchain.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(usersService.getUsers());
    }

    @PostMapping("/user")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        Integer id = usersService.addUser(user);
        if (id != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add user.");
        }
    }
}
