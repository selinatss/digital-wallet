package com.wallet.digitalwallet.utils;

import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.model.response.WalletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WalletResponseConverter {
    public static WalletResponse convert(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        return new WalletResponse(
                wallet.getId(),
                wallet.getWalletName(),
                wallet.getCurrency(),
                wallet.isActiveForShopping(),
                wallet.isActiveForWithdraw(),
                wallet.getBalance(),
                wallet.getUsableBalance(),
                wallet.getCustomer().getId()
        );
    }

    public static List<WalletResponse> convertToResponseList(List<Wallet> walletList) {
        return walletList.stream()
                .map(WalletResponseConverter::convert)
                .collect(Collectors.toList());
    }
}
