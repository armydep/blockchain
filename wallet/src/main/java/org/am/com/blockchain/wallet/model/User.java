package org.am.com.blockchain.wallet.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "wusers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String publicKey;
    private String privateKey;
    private String address;

    public User() {
    }

    public User(String user,
                String pass,
                String address,
                String publicKey,
                String privateKey) {
        this.username = user;
        this.password = pass;
        this.address = address;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}