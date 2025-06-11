package com.cardservice.kafka;

import card.events.CardEvent;
import com.cardservice.event.CardSuccessfullyCreatedEvent;
import com.cardservice.service.GrpcResponseRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final GrpcResponseRegistry responseRegistry;

    @KafkaListener(topics = "card", groupId = "grpc-group")
    public void consumeEvent(byte[] event) {
        try {
            CardEvent cardEvent = CardEvent.parseFrom(event);
            CardSuccessfullyCreatedEvent cardCreatedEvent = CardSuccessfullyCreatedEvent.builder()
                    .userId(cardEvent.getUserId())
                    .cardId(cardEvent.getCardId())
                    .balanceId(cardEvent.getBalanceId())
                    .cardHolderFullName(cardEvent.getCardHolderFullName())
                    .cardNumber(cardEvent.getCardNumber())
                    .build();
            responseRegistry.handleCardSuccess(cardCreatedEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing card event", e);
        }
    }
}
