package org.am.com.tx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxInEntry {
    String txid;
    Integer vout;
    String coinbase;

    @JsonCreator
    public TxInEntry(@JsonProperty("txid") String txid,
                     @JsonProperty("vout") Integer vout,
                     @JsonProperty("coinbase") String coinbase) {
        this.txid = txid;
        this.vout = vout;
        this.coinbase = coinbase;
    }
}
