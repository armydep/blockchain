package org.am.com.blockchainnode.domain.block;

import lombok.Value;

@Value
public class TxOutEntry {
    float value;
    String address;
    int n;
}
