package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.controller.TransactionController;
import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.service.JwtService;
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

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TransactionController transactionController;

    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJzdWIiOiJzZWxpbmF0aXMiLCJpYXQiOjE3NTMwMDIxMTgsImV4cCI6MTc1MzA4ODUxOH0.RmZFmMobpS5CnDNRclWGjOMdYnf0v-iEMwpl04-jqus";


    @Test
    void getTransactionsShouldReturnListOfTransactionsWhenWalletIdIsValid() {
        long walletId = 1L;
        List<TransactionResponse> transactions = List.of(new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null));

        when(transactionService.getTransactionsByWalletId(walletId, token)).thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactions(walletId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void depositShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.deposit(request, token)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.deposit(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void withdrawShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.withdraw(request, token)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.withdraw(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void approveTransactionShouldReturnTransactionResponseWhenRequestIsValid() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.PENDING);
        TransactionResponse response = new TransactionResponse(1234L, 1234L, TransactionType.WITHDRAWAL, TransactionStatus.APPROVED, new BigDecimal(100.0), new BigDecimal(90.0), null);

        when(transactionService.approveTransaction(request, token)).thenReturn(response);

        ResponseEntity<TransactionResponse> result = transactionController.approveTransaction(request, token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void depositShouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);
        String token = "validToken";
        Wallet wallet = new Wallet();
        wallet.setCustomer(new Customer());

        when(jwtService.extractUsername(token)).thenReturn("user");
        when(jwtService.extractRole(token)).thenReturn("CUSTOMER");
        when(transactionService.deposit(request, token)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<TransactionResponse> result = transactionController.deposit(request, token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void withdrawShouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(10.0), PartyType.IBAN);

        when(transactionService.withdraw(request, token)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<TransactionResponse> result = transactionController.withdraw(request, token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void approveTransactionShouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.PENDING);

        when(transactionService.approveTransaction(request, token)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<TransactionResponse> result = transactionController.approveTransaction(request, token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }
}
