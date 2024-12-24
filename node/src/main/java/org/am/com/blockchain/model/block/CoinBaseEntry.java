package org.am.com.blockchain.model.block;

import lombok.Value;

@Value
public class CoinBaseEntry {
    Double value;
    String address;
    Integer n;
    String txid;
}
