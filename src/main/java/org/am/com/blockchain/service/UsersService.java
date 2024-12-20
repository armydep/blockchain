package org.am.com.blockchain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.user.User;
import org.am.com.blockchain.repository.UserRepository;
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
}
