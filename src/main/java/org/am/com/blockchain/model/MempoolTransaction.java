package org.am.com.blockchain.model;

import lombok.Value;
import org.am.com.blockchain.model.block.UTXO;

import java.util.ArrayList;
import java.util.List;

@Value
public class MempoolTransaction implements Cloneable {
    String from;
    String to;
    Double amount;
    Long timestamp;
    List<UTXO> txCoversSum;
    Double change;

    @Override
    public MempoolTransaction clone() {
        return new MempoolTransaction(from, to, amount, timestamp,
                new ArrayList<>(txCoversSum), change);
    }
}
