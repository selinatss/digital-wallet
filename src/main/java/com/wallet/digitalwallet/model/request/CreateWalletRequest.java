package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.Currency;

import java.util.EnumSet;

public record CreateWalletRequest(String walletName, Currency currency, boolean activeForShopping, boolean activeForWithdrawal, long customerId) {
    public CreateWalletRequest {
        if (walletName == null || walletName.isBlank()) {
            throw new IllegalArgumentException("Wallet name cannot be null or blank.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
        if (!EnumSet.of(Currency.TRY, Currency.USD, Currency.EUR).contains(currency)) {
            throw new IllegalArgumentException("Currency must be TL, USD, or EURO.");
        }
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be greater than zero.");
        }
    }
}
