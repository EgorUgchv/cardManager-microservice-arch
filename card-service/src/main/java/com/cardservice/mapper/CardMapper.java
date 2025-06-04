package com.cardservice.mapper;

import card.CardRequest;
import com.cardservice.dto.CardDto;
import com.cardservice.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    CardDto mapToCardDto(Card card);

    Card mapToCard(CardDto cardDto);

    List<CardDto> mapToCardDto(List<Card> cards);
    List<Card> mapToCard(List<CardDto> cardDtos);

    CardDto mapToCardDto(CardRequest cardRequest);
}
