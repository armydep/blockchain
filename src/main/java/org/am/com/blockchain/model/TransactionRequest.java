package org.am.com.blockchain.model;

import lombok.Data;

@Data
public class TransactionRequest {
    private String data;
    private String sender;
    private String recipient;
    private long amount;
    private String signature;
    private String publicKey;
}
