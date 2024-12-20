package org.am.com.blockchain.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class UserRepository {
    @Getter
    private List<User> users;
    private final ObjectMapper objectMapper;
    private final String usersInitFileName;
    private final String usersStorageFileName;

    public UserRepository(ObjectMapper objectMapper,
                          @Value("${init.users.data}") String usersInitFileName,
                          @Value("${storage.users.data}") String usersStorageFileName) {
        this.objectMapper = objectMapper;
        this.usersInitFileName = usersInitFileName;
        this.usersStorageFileName = usersStorageFileName;
    }

    @PostConstruct
    public void init() throws IOException {
        users = loadUsers();
    }

    private List<User> loadUsers() throws IOException {
        ClassPathResource resource = new ClassPathResource(usersInitFileName);
        List<User> data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
        log.info("Loaded users JSON Data: " + data);
        return data;
    }

    public Integer addUser(User user) {
        user.setId(10 * users.size());
        if (isExist(user)) {
            return null;
        }
        users.add(user);
        save();
        return user.getId();
    }

    private boolean isExist(User user) {
        for (User u : users) {
            if (u.getId().equals(user.getId()) || u.getName().equals(user.getName())) {
                log.info("User {} already exists", user.getId());
                return true;
            }
        }
        return false;
    }

    public void save() {
        try {
            objectMapper.writeValue(new File(usersStorageFileName), users);
            log.info("Users saved as JSON in file: " + usersStorageFileName);
        } catch (IOException e) {
            log.error("Error while saving the users to file: " + e.getMessage());
        }
    }

    public User getUserById(Integer userid) {
        for (User user : users) {
            if (user.getId().equals(userid)) {
                log.info("User {} found", userid);
                return user;
            }
        }
        return null;
    }
}
