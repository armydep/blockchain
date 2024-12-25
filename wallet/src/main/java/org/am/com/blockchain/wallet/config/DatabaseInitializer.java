package org.am.com.blockchain.wallet.config;

import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.wallet.model.User;
import org.am.com.blockchain.wallet.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Create and save initial users
            userRepository.save(new User("user1", "$2a$12$wBFC8U9dAuNdptU0Lku5UeQrE1U8TUAwxPmceTAl5yCJx9G3Je9Uu"));
            userRepository.save(new User("user2", "password2"));

            log.info("Initialized database with sample users.");
        };
    }
}
