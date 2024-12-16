package org.am.com.blockchainnode.controller;

import lombok.Value;

@Value
public class MempoolTransaction {
    //public String txid;
    String from;
    String to;
    float amount;
    Long timestamp;
    //walled id
    //node id

    public MempoolTransaction clone() {
        return new MempoolTransaction(this.getFrom(),
                this.getTo(),
                this.getAmount(),
                this.getTimestamp());
    }
}
