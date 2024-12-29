package am.com.blockchain.wallet.model.login;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private final String type = "Bearer";
    private String username;

    public JwtResponse(String jwt, String username) {
        this.token = jwt;
        this.username = username;
    }
}