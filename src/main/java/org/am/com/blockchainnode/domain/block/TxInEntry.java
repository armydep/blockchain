package org.am.com.blockchainnode.domain.block;

import lombok.Value;

@Value
public class TxInEntry {
    String txid;
    int vout;
    String coinbase;
}
