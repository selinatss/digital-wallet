package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.controller.TransactionController;
import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void getTransactionsShouldReturnListOfTransactionsWhenWalletIdIsValid() {
        long walletId = 1L;
        List<TransactionResponse> transactions = List.of(new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null));

        when(transactionService.getTransactionsByWalletId(walletId)).thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactions(walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void getTransactionsShouldReturnInternalServerErrorWhenExceptionOccurs() {
        long walletId = 1L;

        when(transactionService.getTransactionsByWalletId(walletId)).thenThrow(new RuntimeException());

        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactions(walletId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void depositShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), "oppositeParty", PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.deposit(request)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.deposit(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void depositShouldReturnBadRequestWhenExceptionOccurs() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), "oppositeParty", PartyType.IBAN);

        when(transactionService.deposit(request)).thenThrow(new RuntimeException());

        ResponseEntity<TransactionResponse> result = transactionController.deposit(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void withdrawShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), "oppositeParty", PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.withdraw(request)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.withdraw(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void withdrawShouldReturnBadRequestWhenExceptionOccurs() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), "oppositeParty", PartyType.IBAN);

        when(transactionService.withdraw(request)).thenThrow(new RuntimeException());

        ResponseEntity<TransactionResponse> result = transactionController.withdraw(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void approveTransactionShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.PENDING);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.approveTransaction(request)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.approveTransaction(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void approveTransactionShouldReturnBadRequestWhenExceptionOccurs() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.PENDING);

        when(transactionService.approveTransaction(request)).thenThrow(new RuntimeException());

        ResponseEntity<TransactionResponse> result = transactionController.approveTransaction(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }
}
