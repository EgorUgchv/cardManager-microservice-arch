package com.balanceservice.exception;

public class DataEncryptionException extends RuntimeException {
    public DataEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
