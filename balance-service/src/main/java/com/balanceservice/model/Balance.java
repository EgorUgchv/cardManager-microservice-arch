package com.balanceservice.model;

import com.balanceservice.config.Encryptor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Balance {
    @Id
    @GeneratedValue
    private Integer balanceId;
    @Convert(converter = Encryptor.class)
    @Column(name = "encrypted_card_number", nullable = false, columnDefinition = "TEXT", unique = true)
    private String encryptedCardNumber;
    private Long balanceAmount;
}
