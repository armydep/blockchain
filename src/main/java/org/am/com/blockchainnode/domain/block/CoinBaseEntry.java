package org.am.com.blockchainnode.domain.block;

import lombok.Value;

@Value
public class CoinBaseEntry {
    float value;
    String address;
    int n;
    String txid;
}
