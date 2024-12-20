package org.am.com.blockchain.model.block;

import lombok.Value;

@Value
public class TxOutEntry {
    Double value;
    String address;
    Integer n;
}
