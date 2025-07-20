package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.repository.TransactionRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.utils.TransactionResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TransactionResponseConverter transactionResponseConverter;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void depositShouldIncreaseWalletBalanceWhenRequestIsValid() {
        String token = "token";
        String username = "employee1";
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);
        wallet.setCustomer(mockCustomer("employee1", 1L));
        TransactionRequest request = new TransactionRequest(1L, BigDecimal.valueOf(500), PartyType.IBAN);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        TransactionResponse response = transactionService.deposit(request, token);

        assertEquals(BigDecimal.valueOf(500), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(500), wallet.getUsableBalance());
        assertEquals(TransactionType.DEPOSIT, response.transactionType());
    }

    @Test
    void withdrawShouldDecreaseWalletBalanceWhenRequestIsValid() {
        String token = "token";
        String username = "employee1";
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(1000));
        wallet.setActiveForWithdraw(true);

        TransactionRequest request = new TransactionRequest(1L, BigDecimal.valueOf(500), PartyType.IBAN);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        TransactionResponse response = transactionService.withdraw(request, token);

        assertEquals(BigDecimal.valueOf(500), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(500), wallet.getUsableBalance());
        assertEquals(TransactionType.WITHDRAWAL, response.transactionType());
    }

    @Test
    void approveTransactionShouldUpdateTransactionStatusWhenRequestIsValid() {
        String token = "token";
        String username = "employee1";
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(500));

        Transaction transaction = new Transaction();
        transaction.setId(1234L);
        transaction.setWallet(wallet);
        transaction.setAmount(BigDecimal.valueOf(500));
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.PENDING);

        TransactionApprovalRequest request = new TransactionApprovalRequest(transaction.getId(), TransactionStatus.APPROVED);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());

        TransactionResponse response = transactionService.changeTransactionStatus(request, "token");

        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, response.transactionType());
    }

    @Test
    void getTransactionsByWalletIdShouldReturnTransactionResponsesWhenTransactionsExist() {
        String token = "token";
        String username = "employee1";
        long walletId = 1L;
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        Transaction transaction = new Transaction();
        transaction.setId(1234L);
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.APPROVED);
        transaction.setCreatedAt(LocalDateTime.now());

        when(transactionRepository.findByWalletId(walletId)).thenReturn(Optional.of(List.of(transaction)));
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());

        List<TransactionResponse> responses = transactionService.getTransactionsByWalletId(walletId, "token");

        assertEquals(1, responses.size());
        assertEquals(transaction.getId(), responses.get(0).id());
        assertEquals(walletId, responses.get(0).walletId());
        assertEquals(TransactionType.DEPOSIT, responses.get(0).transactionType());
        assertEquals(TransactionStatus.APPROVED, responses.get(0).transactionStatus());
    }

    @Test
    void checkUserAuthorizationShouldThrowExceptionWhenTokenRoleIsNotEmployeeAndWalletDoesNotExist() {
        long walletId = 1L;
        String token = "validToken";

        when(jwtService.extractUsername(token)).thenReturn("user");
        when(jwtService.extractRole(token)).thenReturn("CUSTOMER");
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.checkUserAuthorization(walletId, token));
    }

    @Test
    void checkUserAuthorizationShouldNotThrowExceptionWhenTokenRoleIsEmployee() {
        long walletId = 1L;
        String token = "validToken";

        when(jwtService.extractRole(token)).thenReturn("EMPLOYEE");

        assertDoesNotThrow(() -> transactionService.checkUserAuthorization(walletId, token));
    }

    private Customer mockCustomer(String username, Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUserName(username);
        customer.setTckn("12345678901");
        return customer;
    }

}