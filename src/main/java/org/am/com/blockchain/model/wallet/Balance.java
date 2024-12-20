package org.am.com.blockchain.model.wallet;

import lombok.Getter;
import org.am.com.blockchain.model.block.UTXO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Balance {
    @Getter
    private final String address;
    List<UTXO> utxos;

    public Balance(UTXO utxo) {
        utxos = new ArrayList<>(List.of(utxo.clone()));
        this.address = utxo.getAddress();
    }

    public float getAmount() {
        return (float) utxos.stream().mapToDouble(UTXO::getValue).sum();
    }

    public void addUTXO(UTXO utxo) {
        utxos.add(utxo);
    }

    public List<UTXO> getUtxos() {
        List<UTXO> destination = new ArrayList<>(Collections.nCopies(utxos.size(), null));
        Collections.copy(destination, utxos);
        return destination;
    }
}
