package com.cardservice.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CardAlreadyExistsExceptioin extends RuntimeException {
    public CardAlreadyExistsExceptioin(String m )  {
       super(m);
    }
}
