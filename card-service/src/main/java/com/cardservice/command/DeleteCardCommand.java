package com.cardservice.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class DeleteCardCommand {
    @TargetAggregateIdentifier
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    private String cardNumber;
}
