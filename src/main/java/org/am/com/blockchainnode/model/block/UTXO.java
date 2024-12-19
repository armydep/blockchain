package org.am.com.blockchainnode.model.block;

import lombok.*;

@Builder
@Value
public class UTXO implements Cloneable {
    String tx;
    //todo replace by double
    float value;
    String address;
    int vout;

    @Override
    public UTXO clone() {
        return UTXO.builder().tx(tx).value(value).address(address).build();
    }
}
