package org.am.com.blockchain.model.block;

import lombok.Value;

@Value
public class CoinBaseEntry {
    double value;
    String address;
    int n;
    String txid;
}
