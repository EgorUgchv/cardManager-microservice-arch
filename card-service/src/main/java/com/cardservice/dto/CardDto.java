package com.cardservice.dto;

import com.cardservice.config.MaskData;
import com.cardservice.model.CardStatus;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Integer userId;
    @MaskData
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String cardHolderFullName;
    @Future
    private LocalDate expiryDate;
    private CardStatus cardStatus;
    private Long balanceAmount;
}
