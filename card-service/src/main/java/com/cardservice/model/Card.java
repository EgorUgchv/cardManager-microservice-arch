package com.cardservice.model;

import com.cardservice.config.Encryptor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue
    private Integer cardId;
    private Integer userId;
    @Convert(converter = Encryptor.class)
    @Column(name = "encrypted_card_number", nullable = false, columnDefinition = "TEXT", unique = true)
    private String encryptedCardNumber;
    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderFullName;
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    @Column(name= "card_status", nullable = false, length = 15)
    private CardStatus cardStatus;
}
