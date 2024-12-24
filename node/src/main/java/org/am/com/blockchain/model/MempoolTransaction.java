package org.am.com.blockchain.model;

import lombok.Value;
import org.am.com.blockchain.model.block.UTXO;

import java.util.ArrayList;
import java.util.List;

@Value
public class MempoolTransaction implements Cloneable {
    String sender;
    String recipient;
    Double amount;
    Long timestamp;
    List<UTXO> txCoversSum;
    Double change;

    @Override
    public MempoolTransaction clone() {
        return new MempoolTransaction(sender, recipient, amount, timestamp,
                new ArrayList<>(txCoversSum), change);
    }
}
