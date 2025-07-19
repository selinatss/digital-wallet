package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.repository.TransactionRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public List<TransactionResponse> getTransactionsByWalletId(long walletId) {
        List<Transaction> transactionList = transactionRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("No transactions found for this wallet"));

        List<TransactionResponse> transactionResponses = transactionList.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getWallet().getId(),
                        transaction.getTransactionType(),
                        transaction.getStatus(),
                        transaction.getWallet().getUsableBalance(),
                        transaction.getWallet().getBalance(),
                        transaction.getCreatedAt()))
                .toList();

        return transactionResponses;
    }

    @Transactional
    public TransactionResponse deposit(final TransactionRequest transactionRequest) {
        Wallet wallet = walletRepository.findById(transactionRequest.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(transactionRequest.amount())
                .oppositeParty(transactionRequest.oppositeParty())
                .oppositePartyType(transactionRequest.oppositePartyType())
                .transactionType(TransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        if(transactionRequest.amount().compareTo(Constants.LIMIT_AMOUNT) > 0){
            transaction.setStatus(TransactionStatus.PENDING);
            wallet.setBalance(wallet.getBalance().add(transactionRequest.amount()));
        } else {
            transaction.setStatus(TransactionStatus.APPROVED);
            wallet.setBalance(wallet.getBalance().add(transactionRequest.amount()));
            wallet.setUsableBalance(wallet.getUsableBalance().add(transactionRequest.amount()));
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return new TransactionResponse(transaction.getId(),
                                       transaction.getWallet().getId(),
                                       transaction.getTransactionType(),
                                       transaction.getStatus(),
                                       wallet.getBalance(),
                                       wallet.getUsableBalance(),
                                       LocalDateTime.now());
    }


    @Transactional
    public TransactionResponse withdraw(final TransactionRequest transactionRequest) {
        Wallet wallet = walletRepository.findById(transactionRequest.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

       if(!wallet.isActiveForWithdraw()){
           throw new RuntimeException("Withdrawals are not allowed for this wallet");
       }

        if(wallet.getUsableBalance().compareTo(transactionRequest.amount()) < 0){
           throw new RuntimeException("Insufficient usable balance");
       }

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(transactionRequest.amount())
                .oppositeParty(transactionRequest.oppositeParty())
                .oppositePartyType(transactionRequest.oppositePartyType())
                .transactionType(TransactionType.WITHDRAWAL)
                .createdAt(LocalDateTime.now())
                .build();

        if(transactionRequest.amount().compareTo(Constants.LIMIT_AMOUNT) > 0){
            transaction.setStatus(TransactionStatus.PENDING);
            wallet.setBalance(wallet.getBalance().subtract(transactionRequest.amount()));
        } else {
            transaction.setStatus(TransactionStatus.APPROVED);
            wallet.setBalance(wallet.getBalance().subtract(transactionRequest.amount()));
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(transactionRequest.amount()));
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return new TransactionResponse(transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                wallet.getBalance(),
                wallet.getUsableBalance(),
                LocalDateTime.now());
    }

    @Transactional
    public TransactionResponse approveTransaction(final TransactionApprovalRequest transactionApprovalRequest){
        Transaction transaction = transactionRepository.findById(transactionApprovalRequest.transactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Wallet wallet = transaction.getWallet();

        if(transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Transaction is not pending approval");
        }

        if(transactionApprovalRequest.status() == TransactionStatus.APPROVED){
            transaction.setStatus(TransactionStatus.APPROVED);
            if(transaction.getTransactionType() == TransactionType.DEPOSIT){
                wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
            }else if(transaction.getTransactionType() == TransactionType.WITHDRAWAL){
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }
        } else if (transactionApprovalRequest.status() == TransactionStatus.DENIED) {
            transaction.setStatus(TransactionStatus.DENIED);
            if(transaction.getTransactionType() == TransactionType.DEPOSIT){
                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            }else if(transaction.getTransactionType() == TransactionType.WITHDRAWAL){
                wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
            }
        }else{
            throw new RuntimeException("Invalid transaction status");
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return new TransactionResponse(transaction.getId(),
                transaction.getWallet().getId(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                wallet.getBalance(),
                wallet.getUsableBalance(),
                LocalDateTime.now());
    }
}
