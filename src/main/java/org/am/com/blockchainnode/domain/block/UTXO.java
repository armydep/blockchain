package org.am.com.blockchainnode.domain.block;

import lombok.*;

@Getter
@Builder
public class UTXO {
    String tx;
    float value;
    String address;
    int vout;
    @Setter
    private boolean locked = false;
}
