package com.cardservice.exception;

public class DataSerializationException extends  RuntimeException{
    public DataSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
