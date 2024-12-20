package org.am.com.blockchain.model;

import lombok.Value;
import org.am.com.blockchain.model.block.UTXO;

import java.util.ArrayList;
import java.util.List;

@Value
public class MempoolTransaction {
    //String txid;
    String from;
    String to;
    Float amount;
    Long timestamp;
    List<UTXO> txCoversSum;
    Float change;

    public MempoolTransaction clone() {
        return new MempoolTransaction(from, to, amount, timestamp,
                new ArrayList<>(txCoversSum), change);
    }
}
