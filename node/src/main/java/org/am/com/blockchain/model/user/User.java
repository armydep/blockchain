package org.am.com.blockchain.model.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.am.com.user.Key;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    Integer id;
    @NotEmpty(message = "Username cannot be empty")
    String name;
    @NotEmpty(message = "Password cannot be empty")
    String password;
    List<Key> keys = new ArrayList<>();

    public void addKey(Key key) {
        if (keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);
    }
}
