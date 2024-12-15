package org.am.com.blockchainnode.domain;

import lombok.Data;

@Data
public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
    private Long index;
}
