package com.cardservice.grpc;

import card.CardResponse;
import card.CardServiceGrpc;
import com.cardservice.dto.CardDto;
import com.cardservice.mapper.CardMapper;
import com.cardservice.service.CardService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Set;

@GrpcService
@Slf4j
@AllArgsConstructor
public class CardGrpcService extends CardServiceGrpc.CardServiceImplBase {
    private final CardMapper cardMapper;
    private final CardService cardService;

    @Override
    public void createCard(card.CardRequest cardRequest,
                           StreamObserver<card.CardResponse> responseObserver) {
        log.info("CreateCard request received {}", cardRequest.toString());

        CardDto cardDto = cardMapper.mapToCardDto(cardRequest);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CardDto>> violations = validator.validate(cardDto);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<CardDto> v : violations) {
                sb.append(v.getMessage()).append("; ");
            }

            Status status = Status.INVALID_ARGUMENT.withDescription(sb.toString());
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        int cardId = cardService.createCard(cardDto);

        CardResponse.Builder response = CardResponse.newBuilder()
                .setCardId(cardId)
                .setUserId(cardRequest.getUserId());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
