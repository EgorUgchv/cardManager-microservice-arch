package com.cardservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardSuccessfullyCreatedEvent {
    private Integer userId;
    private Integer cardId;
    private Integer balanceId;
    private String cardHolderFullName;
    private String cardNumber;
}
