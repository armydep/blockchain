package org.am.com.blockchainnode.domain.block;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UTXO {
    String tx;
    float value;
    String address;
    int vout;
}
