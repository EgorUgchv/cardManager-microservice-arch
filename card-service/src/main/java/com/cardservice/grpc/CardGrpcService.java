package com.cardservice.grpc;

import card.CardResponse;
import card.CardServiceGrpc;
import com.cardservice.dto.CardDto;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.exception.CardAlreadyExistsExceptioin;
import com.cardservice.mapper.CardMapper;
import com.cardservice.service.CardService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CardGrpcService extends CardServiceGrpc.CardServiceImplBase {
    private final CardMapper cardMapper;
    private final CardService cardService;
    private final Validator validator;

    @Override
    public void createCard(card.CardRequest cardRequest,
                           StreamObserver<card.CardResponse> responseObserver) {
        log.info("CreateCard request received {}", cardRequest.toString());

        try {
            CardDto cardDto = cardMapper.mapToCardDto(cardRequest);
            Set<ConstraintViolation<CardDto>> violations = validator.validate(cardDto);

            if (!violations.isEmpty()) {
                String errorMessage = buildValidationErrorMessage(violations);
                Status status = Status.INVALID_ARGUMENT.withDescription(errorMessage.toString());
                sendError(responseObserver, status);
                return;
            }

            CardResponseDto cardResponseDto = cardService.createCard(cardDto);

            CardResponse.Builder response = CardResponse.newBuilder()
                    .setUserId(cardRequest.getUserId())
                    .setCardId(cardResponseDto.getCardId())
                    .setBalanceId(cardResponseDto.getBalanceId());

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (CardAlreadyExistsExceptioin e) {
            Status status = Status.ALREADY_EXISTS.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private String buildValidationErrorMessage(Set<ConstraintViolation<CardDto>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; ", "Validation failed: ", ""));
    }

    private void sendError(StreamObserver<?> observer, Status status) {
        observer.onError(status.asRuntimeException());

    }
}
