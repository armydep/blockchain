package am.com.blockchain.common.user;

import lombok.Data;

@Data
public class Key {
    //todo record
    private Integer id;
    private String label;
    private String publicKey;
    private String privateKey;
    private String address;
}
