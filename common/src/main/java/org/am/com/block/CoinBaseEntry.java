package org.am.com.block;

import lombok.Value;

@Value
public class CoinBaseEntry {
    Double value;
    String address;
    Integer n;
    String txid;
}
