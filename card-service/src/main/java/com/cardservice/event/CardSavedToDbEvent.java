package com.cardservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardSavedToDbEvent {
    private Integer cardId;
    private Integer userId;
    private String cardNumber;
    private Long balanceAmount;
    private String cardHolderFullName;
}
