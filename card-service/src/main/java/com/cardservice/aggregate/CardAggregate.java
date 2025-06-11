package com.cardservice.aggregate;

import com.cardservice.command.CreateCardBalanceCommand;
import com.cardservice.command.CreateCardCommand;
import com.cardservice.command.DeleteCardCommand;
import com.cardservice.event.CardBalanceCreatedEvent;
import com.cardservice.event.CardCreatedEvent;
import com.cardservice.event.CardDeletedEvent;
import com.cardservice.model.CardStatus;
import com.cardservice.repository.CardRepository;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Aggregate
public class CardAggregate {
    @AggregateIdentifier
    @Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits")
    @NotBlank
    public String cardNumber;
    private Long balanceAmount;

    private Integer userId;
    @NotBlank
    private String cardHolderFullName;
    @Future
    private LocalDate expiryDate;
    private CardStatus cardStatus;

    public CardAggregate() {
    }

    @CommandHandler
    public CardAggregate(@Validated CreateCardCommand createCardCommand) {
        CardCreatedEvent cardCreatedEvent = CardCreatedEvent.builder()
                .userId(createCardCommand.getUserId())
                .cardNumber(createCardCommand.getCardNumber())
                .cardHolderFullName(createCardCommand.getCardHolderFullName())
                .expiryDate(createCardCommand.getExpiryDate())
                .cardStatus(createCardCommand.getCardStatus())
                .balanceAmount(createCardCommand.getBalanceAmount())
                .build();
        AggregateLifecycle.apply(cardCreatedEvent);
    }

    @EventSourcingHandler
    public void on(@Validated CardCreatedEvent event) {
        this.userId = event.getUserId();
        this.cardNumber = event.getCardNumber();
        this.cardHolderFullName = event.getCardHolderFullName();
        this.expiryDate = event.getExpiryDate();
        this.cardStatus = event.getCardStatus();
    }

    @CommandHandler
    public void handle(@Validated CreateCardBalanceCommand command) {
        CardBalanceCreatedEvent cardBalanceCreatedEvent =
                CardBalanceCreatedEvent
                        .builder()
                        .userId(command.getUserId())
                        .cardId(command.getCardId())
                        .cardNumber(command.getCardNumber())
                        .balanceAmount(command.getBalanceAmount())
                        .build();
        AggregateLifecycle.apply(cardBalanceCreatedEvent);
    }

    @CommandHandler
    public void handle(@Validated DeleteCardCommand deleteCardCommand) {
        CardDeletedEvent cardDeletedEvent = new CardDeletedEvent();
        cardDeletedEvent.setCardNumber(deleteCardCommand.getCardNumber());
        AggregateLifecycle.apply(cardDeletedEvent);
    }
//    @CommandHandler
//    public CardAggregate(@Validated CreateCardBalanceCommand createCardBalanceCommand) {
//        CardCreatedEvent cardCreatedEvent = new C
//        AggregateLifecycle.apply(
//                new CardBalanceCreatedEvent(
//                        createCardBalanceCommand.cardNumber,
//                        createCardBalanceCommand.balanceAmount,
//                        createCardBalanceCommand.balanceStatus
//                )
//        );
//    }

    @EventSourcingHandler
    protected void on(@Validated CardBalanceCreatedEvent event) {
        this.cardNumber = event.getCardNumber();
        this.balanceAmount = event.getBalanceAmount();
    }

}
