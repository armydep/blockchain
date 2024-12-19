package org.am.com.blockchainnode.model;

import lombok.Value;
import org.am.com.blockchainnode.model.block.UTXO;

import java.util.ArrayList;
import java.util.List;

@Value
public class MempoolTransaction {
    //String txid;
    String from;
    String to;
    float amount;
    Long timestamp;
    List<UTXO> txCoversSum;
    float change;

    public MempoolTransaction clone() {
        return new MempoolTransaction(from, to, amount, timestamp,
                new ArrayList<>(txCoversSum), change);
    }
}
