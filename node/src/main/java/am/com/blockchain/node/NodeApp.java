package am.com.blockchain.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NodeApp {

    public static void main(String[] args) {
        SpringApplication.run(NodeApp.class, args);
    }

}
