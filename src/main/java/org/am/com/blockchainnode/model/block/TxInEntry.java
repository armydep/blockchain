package org.am.com.blockchainnode.model.block;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TxInEntry {
    String txid;
    int vout;
    String coinbase;
}
