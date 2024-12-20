package org.am.com.blockchain.model.user;

import lombok.Data;

@Data
public class Key {
    Integer id;
    String label;
    String publicKey;
    String privateKey;
    String address;
}
