package com.cardservice.grpc;

import card.CardRequest;
import card.CardResponse;
import card.CardServiceGrpc;
import com.cardservice.mapper.CardMapper;
import com.cardservice.model.Card;
import com.cardservice.repository.CardRepository;
import com.cardservice.service.CardService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@AllArgsConstructor
public class CardGrpcService extends CardServiceGrpc.CardServiceImplBase {
    private final CardMapper cardMapper;
    private final CardService cardService;

    @Override
    public void createCard(card.CardRequest cardRequest,
                           StreamObserver<card.CardResponse> responseObserver) {
        log.info("createCard request received {}", cardRequest.toString());
int cardId = cardService.createCard(cardRequest);
        CardResponse.Builder response = CardResponse.newBuilder()
                .setCardId(String.valueOf(cardId))
                .setUserId(cardRequest.getUserId());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
