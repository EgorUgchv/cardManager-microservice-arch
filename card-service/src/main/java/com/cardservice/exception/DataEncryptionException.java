package com.cardservice.exception;

public class DataEncryptionException extends RuntimeException {
    public DataEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
