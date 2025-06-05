package com.cardservice.service;

import balance.CardBalanceResponse;
import com.cardservice.dto.CardDto;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.grpc.BalanceGrpcServiceClient;
import com.cardservice.mapper.CardMapper;
import com.cardservice.model.Card;
import com.cardservice.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class CardService {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final BalanceGrpcServiceClient balanceGrpcServiceClient;
    public CardResponseDto createCard(CardDto cardDto) {
        Card card = cardMapper.mapToCard(cardDto);
        card.setEncryptedCardNumber(String.valueOf(cardDto.getCardNumber()));
        Card savedCard = cardRepository.save(card);

        CardBalanceResponse balanceResponse = balanceGrpcServiceClient.createCardBalance(cardDto.getCardNumber(),
                cardDto.getBalanceAmount());
        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setCardId(savedCard.getCardId());
        cardResponseDto.setBalanceId(balanceResponse.getBalanceId());
        return cardResponseDto;
    }
}
