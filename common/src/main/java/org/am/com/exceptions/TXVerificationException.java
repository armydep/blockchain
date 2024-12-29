package org.am.com.exceptions;

public class TXVerificationException extends RuntimeException {

    public TXVerificationException(String message) {
        super(message);
    }

    public TXVerificationException(String message, Exception e) {
        super(message, e);
    }
}
