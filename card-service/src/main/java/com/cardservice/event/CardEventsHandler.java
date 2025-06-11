package com.cardservice.event;

import com.cardservice.command.DeleteCardCommand;
import com.cardservice.grpc.BalanceGrpcServiceClient;
import com.cardservice.model.Card;
import com.cardservice.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardEventsHandler {
    private final CardRepository cardRepository;
    private final BalanceGrpcServiceClient balanceGrpcServiceClient;
    private final CommandGateway commandGateway;
    private final EventBus eventBus;

    @EventHandler
    public void on(CardCreatedEvent event) {
        Card card = new Card();
        card.setUserId(event.getUserId());
        card.setEncryptedCardNumber(event.getCardNumber());
        card.setCardHolderFullName(event.getCardHolderFullName());
        card.setExpiryDate(event.getExpiryDate());
        card.setCardStatus(event.getCardStatus());
        Card savedCard = cardRepository.save(card);

        CardSavedToDbEvent cardSavedToDbEvent = CardSavedToDbEvent.builder()
                .cardId(savedCard.getCardId())
                .userId(savedCard.getUserId())
                .cardNumber(savedCard.getEncryptedCardNumber())
                .cardHolderFullName(savedCard.getCardHolderFullName())
                .balanceAmount(event.getBalanceAmount())
                .build();

        eventBus.publish(GenericEventMessage.asEventMessage(cardSavedToDbEvent));
    }

    @EventHandler
    public void on(CardBalanceCreatedEvent event) {
        try {

            balanceGrpcServiceClient.createCardBalanceAsync(event.getCardNumber(),
                    event.getBalanceAmount(), response -> {
                        eventBus.publish(GenericEventMessage.asEventMessage(
                                CardBalanceSuccessfullyCreatedEvent.builder()
                                        .cardNumber(event.getCardNumber())
                                        .balanceId(response.getBalanceId())
                                        .build()
                        ));
                        log.info("Publish CardBalanceSuccessfullyCreatedEvent cardNumber {}", event.getCardNumber());
                    }
            );
        } catch (Exception e) {
            log.error("Error in CardBalanceCreatedEvent while creating the card balance ", e);
            balanceNotCreatedCommand(event);
        }

    }

    private void balanceNotCreatedCommand(CardBalanceCreatedEvent event) {
        DeleteCardCommand deleteCardCommand =
                DeleteCardCommand
                        .builder()
                        .cardNumber(event.getCardNumber())
                        .build();
        commandGateway.send(deleteCardCommand);
    }

    @EventHandler
    public void on(CardDeletedEvent event) {
        if (cardRepository.existsByEncryptedCardNumber(event.getCardNumber())) {
            cardRepository.deleteByEncryptedCardNumber(event.getCardNumber());
        }
    }
}
