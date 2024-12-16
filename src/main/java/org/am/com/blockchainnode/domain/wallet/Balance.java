package org.am.com.blockchainnode.domain.wallet;

import lombok.Data;
import org.am.com.blockchainnode.domain.block.UTXO;

@Data
public class Balance {
    float balance;
    String address;
    String date = new java.util.Date().toString();

    public Balance(UTXO utxo) {
        this.balance = utxo.getValue();
        this.address = utxo.getAddress();
    }

    public Balance(float v, String address) {
        this.balance = v;
        this.address = address;
    }
}
