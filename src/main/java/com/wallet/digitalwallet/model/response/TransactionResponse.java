package com.wallet.digitalwallet.model.response;

import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransactionResponse(Long id, Long walletId, TransactionType transactionType, TransactionStatus transactionStatus, BigDecimal usableBalance, BigDecimal balance, LocalDateTime createdAt) {
}
