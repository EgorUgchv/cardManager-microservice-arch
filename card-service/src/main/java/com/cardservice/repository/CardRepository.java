package com.cardservice.repository;

import com.cardservice.dto.CardDto;
import com.cardservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    boolean existsByEncryptedCardNumber(String cardNumber);
}
