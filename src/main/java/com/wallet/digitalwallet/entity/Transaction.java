package com.wallet.digitalwallet.entity;

import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Wallet wallet;
    private BigDecimal amount;
    private TransactionType transactionType;
    private PartyType oppositePartyType;
    private String oppositeParty;
    private TransactionStatus status;
    private LocalDateTime createdAt;

}
