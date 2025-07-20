package com.wallet.digitalwallet.model.request;

public record ListWalletRequest(String tckn) {
    public ListWalletRequest {
        if (tckn == null || tckn.length() != 11 || !tckn.matches("\\d{11}")) {
            throw new IllegalArgumentException("TCKN cannot be null or blank");
        }
    }
}
