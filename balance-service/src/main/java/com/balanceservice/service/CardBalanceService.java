package com.balanceservice.service;

import com.balanceservice.dto.CardBalanceDto;
import com.balanceservice.exception.CardBalanceAlreadyExistsException;
import com.balanceservice.mapper.CardBalanceMapper;
import com.balanceservice.model.Balance;
import com.balanceservice.repository.CardBalanceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardBalanceService {
    private final CardBalanceRepository balanceRepository;
    private final CardBalanceMapper cardBalanceMapper;
    @Transactional
    public int createBalance(CardBalanceDto cardBalanceDto) {
        Balance balance = cardBalanceMapper.mapToBalance(cardBalanceDto);
        if(!balanceRepository.existsByEncryptedCardNumber(cardBalanceDto.getCardNumber())){
            Balance savedCardBalance = balanceRepository.save(balance);
            return savedCardBalance.getBalanceId();
        }
        else{
            throw new CardBalanceAlreadyExistsException("The card balance for the " + cardBalanceDto.getCardNumber() + " card number already exists");
        }
    }
}
