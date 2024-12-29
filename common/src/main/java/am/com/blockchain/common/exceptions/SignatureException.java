package am.com.blockchain.common.exceptions;

public class SignatureException extends Exception {

    public SignatureException(String msg) {
        super(msg);
    }

    public SignatureException(Exception e) {
        super(e);
    }
}
