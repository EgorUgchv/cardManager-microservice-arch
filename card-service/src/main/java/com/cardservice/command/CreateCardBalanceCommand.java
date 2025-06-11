package com.cardservice.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateCardBalanceCommand {
    @TargetAggregateIdentifier
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    private final String cardNumber;
    private final Long balanceAmount;
    private final Integer userId;
    private final Integer cardId;
}
