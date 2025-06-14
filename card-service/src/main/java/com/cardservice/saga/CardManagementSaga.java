package com.cardservice.saga;

import com.cardservice.command.CreateCardBalanceCommand;
import com.cardservice.command.DeleteCardCommand;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.event.*;
import com.cardservice.exception.BalanceServiceUnavailableException;
import com.cardservice.kafka.KafkaProducer;
import com.cardservice.service.GrpcResponseRegistry;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.saga.EndSaga;
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
    @Autowired
    private transient EventBus eventBus;
    @Autowired
    private transient GrpcResponseRegistry registry;
    private CardResponseDto cardResponseDto;

    public CardManagementSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "cardNumber")
    public void handle(CardSavedToDbEvent event) {
        log.info("CardCreatedEvent in Saga for cardNumber : {}", event.getCardNumber());
        try {

            CreateCardBalanceCommand createCardBalanceCommand = CreateCardBalanceCommand.builder().userId(event.getUserId()).cardNumber(event.getCardNumber()).balanceAmount(event.getBalanceAmount()).cardId(event.getCardId()).build();
            cardResponseDto = cardResponseDto.builder()
                    .userId(event.getUserId())
                    .cardId(event.getCardId())
                    .cardNumber(event.getCardNumber())
                    .cardHolderFullName(event.getCardHolderFullName())
                    .build();

            commandGateway.send(createCardBalanceCommand);
        }
        catch (Exception e) {
            log.error("Error in createCardBalanceCommand while creating the card balance ", e);
            CardCreationFailedEvent failedEvent =  new CardCreationFailedEvent(event.getCardNumber(), e.getMessage());
            eventBus.publish(GenericEventMessage.asEventMessage(failedEvent));
            balanceNotCreatedCommand(event);

        }
    }
    private void balanceNotCreatedCommand(CardSavedToDbEvent event) {
        DeleteCardCommand deleteCardCommand =
                DeleteCardCommand
                        .builder()
                        .cardNumber(event.getCardNumber())
                        .build();
        commandGateway.send(deleteCardCommand);
    }

    @SagaEventHandler(associationProperty = "cardNumber")
    public void handle(CardCreationFailedEvent event) {
        registry.fail(event.getCardNumber(), Status.INTERNAL.withDescription( event.getErrorMessage()));
    }

    @SagaEventHandler(associationProperty = "cardNumber")
    @EndSaga
    public void handle(CardDeletedEvent event){
        log.info("CardDeletedEvent in Saga for cardNumber : {}", event.getCardNumber());
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
