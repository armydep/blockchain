package am.com.blockchain.wallet.controller.exceptions;

public class NodeException extends RuntimeException {

    public NodeException(String message) {
        super(message);
    }

    public NodeException(String message, Exception e) {
        super(message, e);
    }
}