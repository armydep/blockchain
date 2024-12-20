package org.am.com.blockchain.model.block;

import lombok.Value;

@Value
public class TxOutEntry {
    Float value;
    String address;
    Integer n;
}
