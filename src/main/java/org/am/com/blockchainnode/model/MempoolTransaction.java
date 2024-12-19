package org.am.com.blockchainnode.model;

import lombok.Value;

@Value
public class MempoolTransaction {
    String txid;
    String from;
    String to;
    float amount;
    Long timestamp;
    //walled id
    //node id

    public MempoolTransaction clone() {
        return new MempoolTransaction(
                this.getTxid(),
                this.getFrom(),
                this.getTo(),
                this.getAmount(),
                this.getTimestamp());
    }
}
