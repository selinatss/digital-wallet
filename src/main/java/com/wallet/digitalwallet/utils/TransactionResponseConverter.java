package com.wallet.digitalwallet.utils;

import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionResponseConverter {

    public static TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getWallet().getUsableBalance(),
                transaction.getWallet().getBalance(),
                transaction.getCreatedAt()
        );
    }

    public static List<TransactionResponse> convertToResponseList(List<Transaction> transactionList) {
        return transactionList.stream()
                .map(TransactionResponseConverter::convertToResponse)
                .collect(Collectors.toList());
    }
}
