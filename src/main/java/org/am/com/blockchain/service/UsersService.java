package org.am.com.blockchain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.user.Key;
import org.am.com.blockchain.model.user.User;
import org.am.com.blockchain.repository.UserRepository;
import org.am.com.blockchain.util.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UsersService {
    private final UserRepository usersRepository;

    public List<User> getUsers() {
        return usersRepository.getUsers();
    }

    public Integer addUser(User user) {
        return usersRepository.addUser(user);
    }

    public Integer generateKey(Integer userid, String label) throws Exception {
        User user = usersRepository.getUserById(userid);
        List<Key> keys = user.getKeys();
        if (keys != null) {
            for (Key key : keys) {
                if (key.getLabel().equals(label)) {
                    log.info("Key {} already exists", key);
                    return null;
                }
            }
        }
        Key key = new Key();
        key.setLabel(label);
        key.setId(keys == null ? 0 : keys.size() * 10 + 1);
        CryptoUtil.generateKeys(key);
        user.addKey(key);
        usersRepository.save();
        return key.getId();
    }
}
