package am.com.blockchain.node.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import am.com.blockchain.node.model.user.User;
import am.com.blockchain.node.service.UsersService;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add user");
        }
    }

    @PostMapping("/key/{userid}/{label}")
    public ResponseEntity<String> generateKey(@PathVariable Integer userid,
                                              @PathVariable String label) throws Exception {
        Integer id = usersService.generateKey(userid, label);
        if (id != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to generate key");
        }
    }
}
