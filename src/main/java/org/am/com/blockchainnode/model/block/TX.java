package org.am.com.blockchainnode.model.block;

import lombok.Value;

import java.util.List;

@Value
public class TX {
    String txid;
    List<TxInEntry> vin;
    List<TxOutEntry> vout;
}
