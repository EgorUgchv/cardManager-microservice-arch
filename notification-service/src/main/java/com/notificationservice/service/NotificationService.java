package com.notificationservice.service;

import card.events.CardEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Collections.replaceAll;

@Service
@Slf4j
public class NotificationService {
    //TODO: Use JavaMailSender
    public void sendCreateCardNotification(CardEvent event) {
        String maskedCardNumber = event.getCardNumber().toString().replaceAll("\\d(?=\\d{4})", "*");
        String message =
                """
                        ===================================================
                        Card Created Notification
                        ----------------------------------------------------
                        Dear %s,
                        Your card with card number: %s has been created successfully.
                        
                        Thanks,
                        CardManagement Team
                        ===================================================
               """.formatted(event.getCardHolderFullName(), maskedCardNumber);
        log.info("\n{}", message);
    }
}
