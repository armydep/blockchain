package am.com.blockchain.common.exceptions;

public class MissingFeeException extends Exception {
    private final String txid;

    public MissingFeeException(String txid, Double fee) {
        super("Illegal fee: " + fee + ". txid: " + txid);
        this.txid = txid;
    }

    public String getTxid() {
        return txid;
    }
}
