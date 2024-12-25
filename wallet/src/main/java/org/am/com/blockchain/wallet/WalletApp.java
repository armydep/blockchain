package org.am.com.blockchain.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableJpaRepositories(basePackages = "org.am.com.blockchain.wallet.repository")
//@EnableScheduling

//(exclude =
//{org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})


@SpringBootApplication
public class WalletApp {
    public static void main(String[] args) {
        SpringApplication.run(WalletApp.class, args);
    }
}
