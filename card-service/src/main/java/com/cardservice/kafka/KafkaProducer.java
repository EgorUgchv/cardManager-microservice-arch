package com.cardservice.kafka;

import card.events.CardEvent;
import com.cardservice.model.Card;
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

    public void sendEvent(Card card) {
        CardEvent event = CardEvent.newBuilder()
                .setCardId(card.getCardId())
                .setCardHolderFullName(card.getCardHolderFullName())
                .setCardNumber(card.getEncryptedCardNumber())
                .setExpiryDate(String.valueOf(card.getExpiryDate()))
                .setCardStatus(String.valueOf(card.getCardStatus()))
                .setEventType("CARD_CREATED")
                .build();

        try {

            kafkaTemplate.send("card", event.toByteArray());

        } catch (Exception e) {
            log.error("Error sending CardCreated event: {}", event);
        }
    }
}
