package am.com.blockchain.wallet.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "wusers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String publicKey;
    private String privateKey;
    private String address;
    private String plain;

    public User() {
    }

    public User(String user,
                String pass,
                String address,
                String publicKey,
                String privateKey,
                String plain) {
        this.username = user;
        this.password = pass;
        this.address = address;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.plain = plain;
    }
}