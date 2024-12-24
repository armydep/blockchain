package org.am.com.blockchain.model.user;

import lombok.Data;

@Data
public class Key {
    private Integer id;
    private String label;
    private String publicKey;
    private String privateKey;
    private String address;
}
