package com.wallet.digitalwallet.model.request;

public record ListWalletRequest(long customerId) {
    public ListWalletRequest {
       if(customerId <= 0) {
           throw new IllegalArgumentException("Customer ID must be greater than zero.");
       }
    }
}
