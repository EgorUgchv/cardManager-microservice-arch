package com.cardservice.exception;

public class CardAlreadyExistsException extends RuntimeException {
    public CardAlreadyExistsException(String m )  {
       super(m);
    }
}
