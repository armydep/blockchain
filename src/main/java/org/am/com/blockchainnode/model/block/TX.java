package org.am.com.blockchainnode.model.block;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class TX {
    String txid;
    List<TxInEntry> vin;
    List<TxOutEntry> vout;

    public TX(String txid, List<TxInEntry> vin, List<TxOutEntry> vout) {
        this.txid = txid;
        this.vin = Collections.unmodifiableList(vin);
        this.vout = Collections.unmodifiableList(vout);
    }
}
