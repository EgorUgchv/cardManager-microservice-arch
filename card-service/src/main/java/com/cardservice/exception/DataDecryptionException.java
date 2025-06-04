package com.cardservice.exception;

public class DataDecryptionException extends RuntimeException {
    public DataDecryptionException(String message, Throwable cause) {
        super(message,cause);
    }
}
