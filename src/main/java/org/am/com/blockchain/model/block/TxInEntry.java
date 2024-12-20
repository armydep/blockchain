package org.am.com.blockchain.model.block;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxInEntry {
    String txid;
    Integer vout;
    String coinbase;
}
