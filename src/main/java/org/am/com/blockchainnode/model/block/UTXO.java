package org.am.com.blockchainnode.model.block;

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
