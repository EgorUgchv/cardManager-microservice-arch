package com.balanceservice.exception;

public class CipherInitializationException  extends RuntimeException {
    public CipherInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
