package org.am.com.blockchain.model.block;

import lombok.Value;

@Value
public class CoinBaseEntry {
    float value;
    String address;
    int n;
    String txid;
}
