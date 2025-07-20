package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.Currency;

public record CreateWalletRequest(String walletName, Currency currency, boolean activeForShopping, boolean activeForWithdrawal, String tckn) {
    public CreateWalletRequest {
        if (walletName == null || walletName.isBlank()) {
            throw new IllegalArgumentException("Wallet name cannot be null or blank.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
        if (tckn == null || tckn.length() != 11 || !tckn.matches("\\d{11}")) {
            throw new IllegalArgumentException("TCKN cannot be null or blank");
        }
    }
}
