package com.cardservice.service;

import card.CardResponse;
import com.cardservice.command.CreateCardCommand;
import com.cardservice.dto.CardDto;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.exception.CardAlreadyExistsException;
import com.cardservice.grpc.BalanceGrpcServiceClient;
import com.cardservice.kafka.KafkaProducer;
import com.cardservice.mapper.CardMapper;
import com.cardservice.repository.CardRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final BalanceGrpcServiceClient balanceGrpcServiceClient;
    private final KafkaProducer kafkaProducer;
    private final CommandGateway commandGateway;
private final GrpcResponseRegistry responseRegistry;

    public void createCard(CardDto cardDto, StreamObserver<CardResponse> observer) {
        if (cardRepository.existsByEncryptedCardNumber(cardDto.getCardNumber())) {
            throw new CardAlreadyExistsException("Card number already exists: " + cardDto.getCardNumber());
        }
//        Card card = cardMapper.mapToCard(cardDto);
//        card.setEncryptedCardNumber(String.valueOf(cardDto.getCardNumber()));
//        Card savedCard = cardRepository.save(card);
//
//        commandGateway.send(new CreateCardBalanceCommand(savedCard.getCardId(),
//                savedCard.getCardHolderFullName(),
//                savedCard.getEncryptedCardNumber(),
//                savedCard.getExpiryDate(),
//                savedCard.getCardStatus()));
//
//        CardBalanceResponse balanceResponse = balanceGrpcServiceClient.createCardBalance(cardDto.getCardNumber(),
//                cardDto.getBalanceAmount());
//        CardResponseDto cardResponseDto = new CardResponseDto();
//        cardResponseDto.setCardId(savedCard.getCardId());
//        cardResponseDto.setBalanceId(balanceResponse.getBalanceId());

        CreateCardCommand createCardCommand
                = CreateCardCommand
                .builder()
                .userId(cardDto.getUserId())
                .cardNumber(cardDto.getCardNumber())
                .cardHolderFullName(cardDto.getCardHolderFullName())
                .expiryDate(cardDto.getExpiryDate())
                .cardStatus(cardDto.getCardStatus())
                .balanceAmount(cardDto.getBalanceAmount())
                .build();
        String cardNumber = cardDto.getCardNumber();
        responseRegistry.register(cardNumber, observer);
        commandGateway.send(createCardCommand);

    }
}
