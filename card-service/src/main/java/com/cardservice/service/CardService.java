package com.cardservice.service;

import com.cardservice.dto.CardDto;
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
    public int createCard(CardDto cardDto) {
        Card card = cardMapper.mapToCard(cardDto);
        card.setEncryptedCardNumber(String.valueOf(cardDto.getCardNumber()));
        Card savedCard = cardRepository.save(card);
        return savedCard.getCardId();
    }
}
