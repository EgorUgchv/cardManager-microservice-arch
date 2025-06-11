package com.cardservice.repository;

import com.cardservice.dto.CardDto;
import com.cardservice.dto.CardResponseDto;
import com.cardservice.model.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    boolean existsByEncryptedCardNumber(String cardNumber);
void deleteByEncryptedCardNumber(String cardNumber);


   Optional<Card>  getCardByEncryptedCardNumber(String cardNumber);

}
