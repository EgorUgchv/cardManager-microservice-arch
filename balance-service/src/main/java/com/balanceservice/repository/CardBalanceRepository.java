package com.balanceservice.repository;

import com.balanceservice.model.Balance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBalanceRepository extends JpaRepository<Balance, Integer> {
    boolean existsByEncryptedCardNumber(@Pattern(regexp = "\\d{16}", message = "The card number must contain only 16 digits") @NotBlank String cardNumber);
}
