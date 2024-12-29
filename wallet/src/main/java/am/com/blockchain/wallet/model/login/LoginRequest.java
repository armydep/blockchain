package am.com.blockchain.wallet.model.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}