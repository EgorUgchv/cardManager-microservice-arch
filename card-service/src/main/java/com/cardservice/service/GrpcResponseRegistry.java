package com.cardservice.service;

import card.CardResponse;
import card.events.CardEvent;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.event.CardSuccessfullyCreatedEvent;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class GrpcResponseRegistry {
    private final Map<String, StreamObserver<CardResponse>> pendingResponses = new ConcurrentHashMap<>();
    public void register(String cardNumber, StreamObserver<CardResponse> observer) {
        pendingResponses.put(cardNumber, observer);
    }

    public void handleCardSuccess(CardSuccessfullyCreatedEvent cardEvent) {
        String cardNumber = cardEvent.getCardNumber();
        log.info("Received Card successful event with card number {}", cardNumber);
        StreamObserver<CardResponse> observer = pendingResponses.remove(cardNumber);
        if(observer != null) {
            CardResponse.Builder response = CardResponse.newBuilder()
                    .setCardId(cardEvent.getCardId())
                    .setUserId(cardEvent.getUserId())
                    .setBalanceId(cardEvent.getCardId());

            observer.onNext(response.build());
            observer.onCompleted();
        }
    }

    public void fail(String cardNumber, Status status) {
        StreamObserver<CardResponse> observer = pendingResponses.remove(cardNumber);
        if(observer != null) {
            observer.onError(status.asException());
        }
    }
}
