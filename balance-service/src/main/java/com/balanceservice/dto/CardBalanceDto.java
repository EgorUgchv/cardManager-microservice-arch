package com.balanceservice.dto;

import jakarta.validation.constraints.Min;
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
    @Min(value = 0, message = "The balance amount must be no less than 0")
    private Long balanceAmount;
}
