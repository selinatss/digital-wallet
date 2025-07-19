package com.wallet.digitalwallet.entity;

import com.wallet.digitalwallet.enums.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String walletName;
    private Currency currency;
    private boolean activeForShopping;
    private boolean activeForWithdraw;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal usableBalance = BigDecimal.ZERO;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name= "customer_id")
    private Customer customer;
}
