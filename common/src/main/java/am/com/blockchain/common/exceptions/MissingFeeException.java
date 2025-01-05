package am.com.blockchain.common.exceptions;

public class MissingFeeException extends Exception {
    private final String txid;

    public MissingFeeException(String txid, Double fee) {
        super(String.format("Illegal fee: %s. txid: %s", fee, txid));
        this.txid = txid;
    }

    public String getTxId() {
        return txid;
    }
}
