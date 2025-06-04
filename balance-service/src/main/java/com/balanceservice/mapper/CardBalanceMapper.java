package com.balanceservice.mapper;

import balance.CreateCardBalanceRequest;
import com.balanceservice.dto.CardBalanceDto;
import com.balanceservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardBalanceMapper {
    CardBalanceDto mapToCardBalanceDto(CreateCardBalanceRequest createCardBalanceRequest);

    @Mapping(target = "encryptedCardNumber", source = "cardNumber")
    Balance mapToBalance(CardBalanceDto cardBalanceDto);
}
