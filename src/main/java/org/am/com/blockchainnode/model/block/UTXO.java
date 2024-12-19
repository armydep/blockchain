package org.am.com.blockchainnode.model.block;

import lombok.*;

@Value
public class UTXO implements Cloneable {
    String tx;
    //todo replace by double
    Float value;
    String address;
    Integer vout;

    @Override
    public UTXO clone() {
        return new UTXO(tx, value, address, vout);
    }
}
