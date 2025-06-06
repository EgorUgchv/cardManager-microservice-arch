package com.notificationservice.kafka;

import card.events.CardEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import com.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationService notificationService;
    @KafkaListener(topics="card", groupId = "notification-service")
    public void consumeEvent(byte[] event){
        try {
            CardEvent cardEvent = CardEvent.parseFrom(event);
            log.info("Received Card Event: [CardFullName = {}, CardNumber = {}]", cardEvent.getCardHolderFullName(), cardEvent.getCardNumber());
            notificationService.sendCreateCardNotification(cardEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }
}
