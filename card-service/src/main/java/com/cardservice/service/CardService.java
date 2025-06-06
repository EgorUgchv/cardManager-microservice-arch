package com.cardservice.service;

import balance.CardBalanceResponse;
import com.cardservice.dto.CardDto;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.exception.CardAlreadyExistsExceptioin;
import com.cardservice.grpc.BalanceGrpcServiceClient;
import com.cardservice.kafka.KafkaProducer;
import com.cardservice.mapper.CardMapper;
import com.cardservice.model.Card;
import com.cardservice.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final BalanceGrpcServiceClient balanceGrpcServiceClient;
    private final KafkaProducer kafkaProducer;

    public CardResponseDto createCard(CardDto cardDto) {
        if (cardRepository.existsByEncryptedCardNumber(cardDto.getCardNumber())) {
            throw new CardAlreadyExistsExceptioin("Card number already exists: " + cardDto.getCardNumber());
        }
        Card card = cardMapper.mapToCard(cardDto);
        card.setEncryptedCardNumber(String.valueOf(cardDto.getCardNumber()));
        Card savedCard = cardRepository.save(card);

        CardBalanceResponse balanceResponse = balanceGrpcServiceClient.createCardBalance(cardDto.getCardNumber(),
                cardDto.getBalanceAmount());
        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setCardId(savedCard.getCardId());
        cardResponseDto.setBalanceId(balanceResponse.getBalanceId());

        kafkaProducer.sendEvent(card);
        return cardResponseDto;
    }
}
