package com.cardservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardCreationFailedEvent {
    private String cardNumber;
    private String errorMessage;
}
