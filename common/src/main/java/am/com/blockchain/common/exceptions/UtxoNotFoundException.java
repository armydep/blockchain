package am.com.blockchain.common.exceptions;

public class UtxoNotFoundException extends Exception {
    private final String txid;

    public UtxoNotFoundException(String txid) {
        super("Utxo tx: " + txid);
        this.txid = txid;
    }

    public String getTxId() {
        return txid;
    }
}
