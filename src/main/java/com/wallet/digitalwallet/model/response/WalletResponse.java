package com.wallet.digitalwallet.model.response;

import com.wallet.digitalwallet.enums.Currency;

import java.math.BigDecimal;

public record WalletResponse(Long walletId, String walletName, Currency currency, boolean activeForShopping, boolean activeForWithdrawal, BigDecimal balance, BigDecimal usableBalance,String tckn) {
}
