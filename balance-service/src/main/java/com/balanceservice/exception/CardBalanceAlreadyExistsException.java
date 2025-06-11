package com.balanceservice.exception;

public class CardBalanceAlreadyExistsException extends RuntimeException {
    public CardBalanceAlreadyExistsException(String m)  {
       super(m);
    }
}
