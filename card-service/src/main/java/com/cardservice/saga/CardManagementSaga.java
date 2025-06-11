package com.cardservice.saga;

import com.cardservice.command.CreateCardBalanceCommand;
import com.cardservice.config.Encryptor;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.event.CardBalanceSuccessfullyCreatedEvent;
import com.cardservice.event.CardSavedToDbEvent;
import com.cardservice.event.CardSuccessfullyCreatedEvent;
import com.cardservice.kafka.KafkaProducer;
import com.cardservice.repository.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class CardManagementSaga {
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient KafkaProducer kafkaProducer;
    private CardResponseDto cardResponseDto;

    public CardManagementSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "cardNumber")
    public void handle(CardSavedToDbEvent event) {
        log.info("CardCreatedEvent in Saga for cardNumber : {}", event.getCardNumber());

        CreateCardBalanceCommand createCardBalanceCommand = CreateCardBalanceCommand.builder().userId(event.getUserId()).cardNumber(event.getCardNumber()).balanceAmount(event.getBalanceAmount()).cardId(event.getCardId()).build();
        cardResponseDto = cardResponseDto.builder()
                .userId(event.getUserId())
                .cardId(event.getCardId())
                .cardNumber(event.getCardNumber())
                .cardHolderFullName(event.getCardHolderFullName())
                .build();

        commandGateway.send(createCardBalanceCommand);
    }

    @SagaEventHandler(associationProperty = "cardNumber")
    public void handle(CardBalanceSuccessfullyCreatedEvent event) {
        log.info("CardBalanceCreatedEvent in Saga for cardNumber : {}", event.getCardNumber());
        if (this.cardResponseDto != null) {
            this.cardResponseDto.setBalanceId(event.getBalanceId());
            sendFinalEvent(cardResponseDto);
        }
    }

    private void sendFinalEvent(CardResponseDto cardResponseDto) {
        CardSuccessfullyCreatedEvent cardCreatedEvent = CardSuccessfullyCreatedEvent.builder()
                .cardId(cardResponseDto.getCardId())
                .userId(cardResponseDto.getUserId())
                .balanceId(cardResponseDto.getBalanceId())
                .cardHolderFullName(cardResponseDto.getCardNumber())
                .cardNumber(cardResponseDto.getCardNumber())
                .build();
        kafkaProducer.sendEvent(cardCreatedEvent);

        log.info("Card successfully created in Saga for cardNumber : {}", cardCreatedEvent.getCardNumber());
        SagaLifecycle.end();
    }

}
