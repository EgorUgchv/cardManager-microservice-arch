package com.balanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardBalanceDto {
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    private String cardNumber;
    private Integer balanceAmount;
}
