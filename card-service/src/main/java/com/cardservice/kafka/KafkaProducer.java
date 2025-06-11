package com.cardservice.kafka;

import card.events.CardEvent;
import com.cardservice.event.CardSuccessfullyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(CardSuccessfullyCreatedEvent cardCreatedEvent) {
        CardEvent event = CardEvent.newBuilder()
                .setCardId(cardCreatedEvent.getCardId())
                .setUserId(cardCreatedEvent.getUserId())
                .setBalanceId(cardCreatedEvent.getBalanceId())
                .setCardHolderFullName(cardCreatedEvent.getCardHolderFullName())
                .setCardNumber(cardCreatedEvent.getCardNumber())
                .setEventType("CARD_CREATED")
                .build();
        try {
            kafkaTemplate.send("card", event.toByteArray());

        } catch (Exception e) {
            log.error("Error sending CardCreated event: {}", event);
        }
    }
}
