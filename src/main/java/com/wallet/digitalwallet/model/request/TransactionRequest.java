package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.PartyType;

import java.math.BigDecimal;

public record TransactionRequest(
        long walletId,
        BigDecimal amount,
        String oppositeParty,
        PartyType oppositePartyType
){}
