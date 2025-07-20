package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(
        long walletId,
        BigDecimal amount,
        PartyType oppositePartyType
){
    public TransactionRequest {
        if (walletId <= 0) {
            throw new IllegalArgumentException("Wallet ID must be greater than zero.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
        if (oppositePartyType == null) {
            throw new IllegalArgumentException("Opposite party type cannot be null.");
        }
    }
}
