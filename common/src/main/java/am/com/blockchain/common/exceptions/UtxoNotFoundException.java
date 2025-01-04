package am.com.blockchain.common.exceptions;

import am.com.blockchain.common.tx.TX;

public class UtxoNotFoundException extends Exception {
    private final TX tx;

    public UtxoNotFoundException(TX tx) {
        super("Utxo tx: " + tx);
        this.tx = tx;
    }

    public TX getTX() {
        return tx;
    }
}
