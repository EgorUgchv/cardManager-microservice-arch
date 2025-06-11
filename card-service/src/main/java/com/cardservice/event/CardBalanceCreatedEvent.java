package com.cardservice.event;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class CardBalanceCreatedEvent {
    private final Integer userId;
    private final Integer cardId;
    private final Integer balanceId;
    private final String cardNumber;
    private final Long balanceAmount;
}
