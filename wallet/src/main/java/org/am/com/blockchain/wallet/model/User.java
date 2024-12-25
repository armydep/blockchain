package org.am.com.blockchain.wallet.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String address;

    public User() {
    }

    public User(String user1, String password1) {
        this.username = user1;
        this.password = password1;
    }
}