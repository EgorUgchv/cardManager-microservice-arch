package com.cardservice.event;

import com.cardservice.config.Encryptor;
import com.cardservice.model.CardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CardCreatedEvent {

    private Integer userId;
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String cardHolderFullName;
    @Future
    private LocalDate expiryDate;
    private CardStatus cardStatus;
    @Min(0)
    private Long balanceAmount;
}
