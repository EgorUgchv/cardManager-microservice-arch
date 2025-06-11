package com.cardservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardBalanceSuccessfullyCreatedEvent {
    private Integer balanceId;
    private String cardNumber;
}
