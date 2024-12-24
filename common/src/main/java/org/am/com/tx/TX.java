package org.am.com.tx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class TX {
    String txid;
    List<TxInEntry> vin;
    List<TxOutEntry> vout;

    @JsonCreator
    public TX(@JsonProperty("txid") String txid,
              @JsonProperty("vin") List<TxInEntry> vin,
              @JsonProperty("vout") List<TxOutEntry> vout) {
        this.txid = txid;
        this.vin = Collections.unmodifiableList(vin);
        this.vout = Collections.unmodifiableList(vout);
    }
}
