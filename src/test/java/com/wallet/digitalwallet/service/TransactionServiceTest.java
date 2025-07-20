package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.repository.TransactionRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.service.TransactionService;
import com.wallet.digitalwallet.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    String token = "validToken";

    @Test
    void depositShouldIncreaseWalletBalanceAndUsableBalanceWhenAmountIsBelowLimit() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        TransactionRequest request = new TransactionRequest(1L, BigDecimal.valueOf(500),  PartyType.IBAN);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        TransactionResponse response = transactionService.deposit(request, token);

        assertEquals(BigDecimal.valueOf(500), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(500), wallet.getUsableBalance());
        assertEquals(TransactionStatus.APPROVED, response.transactionStatus());
    }

    @Test
    void depositShouldSetTransactionToPendingWhenAmountExceedsLimit() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);

        TransactionRequest request = new TransactionRequest(1L, Constants.LIMIT_AMOUNT.add(BigDecimal.ONE),  PartyType.PAYMENT);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        TransactionResponse response = transactionService.deposit(request, token);

        assertEquals(Constants.LIMIT_AMOUNT.add(BigDecimal.ONE), wallet.getBalance());
        assertEquals(BigDecimal.ZERO, wallet.getUsableBalance());
        assertEquals(TransactionStatus.PENDING, response.transactionStatus());
    }

    @Test
    void withdrawShouldDecreaseWalletBalanceAndUsableBalanceWhenAmountIsBelowLimit() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(1000));
        wallet.setActiveForWithdraw(true);

        TransactionRequest request = new TransactionRequest(1L, BigDecimal.valueOf(500),  PartyType.IBAN);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        TransactionResponse response = transactionService.withdraw(request, token);

        assertEquals(BigDecimal.valueOf(500), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(500), wallet.getUsableBalance());
        assertEquals(TransactionStatus.APPROVED, response.transactionStatus());
    }

    @Test
    void withdrawShouldThrowExceptionWhenUsableBalanceIsInsufficient() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(100));
        wallet.setActiveForWithdraw(true);

        TransactionRequest request = new TransactionRequest(1L, BigDecimal.valueOf(500),  PartyType.IBAN);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(RuntimeException.class, () -> transactionService.withdraw(request, token));
    }

    @Test
    void approveTransactionShouldUpdateWalletAndTransactionForApprovedDeposit() {
        Wallet wallet = new Wallet();
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

        TransactionResponse response = transactionService.approveTransaction(request, token);

        assertEquals(BigDecimal.valueOf(1000), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(1000), wallet.getUsableBalance());
        assertEquals(TransactionStatus.APPROVED, response.transactionStatus());
    }

    @Test
    void approveTransactionShouldThrowExceptionForInvalidTransactionStatus() {
        Transaction transaction = new Transaction();

        TransactionApprovalRequest request = new TransactionApprovalRequest(1234L, TransactionStatus.DENIED);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        assertThrows(RuntimeException.class, () -> transactionService.approveTransaction(request, token));
    }
}
