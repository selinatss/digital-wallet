package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.TransactionStatus;

public record TransactionApprovalRequest(long transactionId, TransactionStatus status) {
    public TransactionApprovalRequest {
        if (transactionId <= 0) {
            throw new IllegalArgumentException("Transaction ID must be greater than zero.");
        }
        if (status == null ) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        if (!TransactionStatus.APPROVED.equals(status) && !TransactionStatus.DENIED.equals(status)) {
            throw new IllegalArgumentException("Status must be either APPROVED or DENIED.");
        }
    }
}
