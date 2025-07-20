package com.wallet.digitalwallet.controller;

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
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;


    @InjectMocks
    private TransactionController transactionController;

    String token = "Bearer testToken";


    @Test
    void getTransactionsShouldReturnListOfTransactionsWhenWalletIdIsValid() {
        long walletId = 1L;
        List<TransactionResponse> transactions = List.of(new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null));

        when(transactionService.getTransactionsByWalletId(walletId, "testToken")).thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactions(walletId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void depositShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.deposit(request, "testToken")).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.deposit(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void withdrawShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.withdraw(request, "testToken")).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.withdraw(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void approveTransactionShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.APPROVED);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.changeTransactionStatus(request, "testToken")).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.approveTransaction(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getTransactionsShouldReturnEmptyListWhenNoTransactionsExist() {
        long walletId = 1L;

        when(transactionService.getTransactionsByWalletId(walletId, "testToken")).thenReturn(List.of());

        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactions(walletId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void withdrawShouldThrowExceptionWhenAmountExceedsUsableBalance() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(1000.0), PartyType.IBAN);

        when(transactionService.withdraw(request, "testToken"))
                .thenThrow(new IllegalArgumentException("Insufficient balance"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> transactionController.withdraw(request, token)
        );

        assertEquals("Insufficient balance", thrown.getMessage());
    }


    @Test
    void approveTransactionShouldReturnBadRequestWhenTransactionIdIsInvalid() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(9999L, TransactionStatus.APPROVED);

        when(transactionService.changeTransactionStatus(request, "testToken"))
                .thenThrow(new IllegalArgumentException("Invalid transaction ID"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> transactionController.approveTransaction(request, token)
        );

        assertEquals("Invalid transaction ID", thrown.getMessage());
    }
}
