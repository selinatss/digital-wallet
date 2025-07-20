package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Transaction;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.enums.TransactionStatus;
import com.wallet.digitalwallet.enums.TransactionType;
import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.repository.TransactionRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.utils.Constants;
import com.wallet.digitalwallet.utils.ErrorMessages;
import com.wallet.digitalwallet.utils.TransactionResponseConverter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionResponseConverter transactionResponseConverter;
    private final JwtService jwtService;

    public List<TransactionResponse> getTransactionsByWalletId(long walletId, String token) {
        checkUserAuthorization(walletId, token);
        List<Transaction> transactionList = transactionRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.NO_TRANSACTIONS_FOUND));

        return transactionResponseConverter.convertToResponseList(transactionList);
    }


    @Transactional
    public TransactionResponse deposit(final TransactionRequest transactionRequest, String token) {
        checkUserAuthorization(transactionRequest.walletId(), token);
        Wallet wallet = walletRepository.findById(transactionRequest.walletId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.WALLET_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(transactionRequest.amount())
                .oppositeParty("")
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
        return transactionResponseConverter.convertToResponse(transaction);
    }


    @Transactional
    public TransactionResponse withdraw(final TransactionRequest transactionRequest, String token) {
        checkUserAuthorization(transactionRequest.walletId(), token);
        Wallet wallet = walletRepository.findById(transactionRequest.walletId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.WALLET_NOT_FOUND));

       if(!wallet.isActiveForWithdraw() || !wallet.isActiveForShopping()) {
           throw new RuntimeException(ErrorMessages.WITHDRAWALS_ARE_NOT_ALLOWED_FOR_THIS_WALLET);
       }else if ( !wallet.isActiveForShopping()){
           throw new RuntimeException(ErrorMessages.SHOPPING_IS_NOT_ALLOWED_FOR_THIS_WALLET);
       }

        if(wallet.getUsableBalance().compareTo(transactionRequest.amount()) < 0){
           throw new RuntimeException(ErrorMessages.INSUFFICIENT_USABLE_BALANCE);
       }

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(transactionRequest.amount())
                .oppositeParty("")
                .oppositePartyType(transactionRequest.oppositePartyType())
                .transactionType(TransactionType.WITHDRAWAL)
                .createdAt(LocalDateTime.now())
                .build();

        if(transactionRequest.amount().compareTo(Constants.LIMIT_AMOUNT) > 0){
            transaction.setStatus(TransactionStatus.PENDING);
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(transactionRequest.amount()));
        } else {
            transaction.setStatus(TransactionStatus.APPROVED);
            wallet.setBalance(wallet.getBalance().subtract(transactionRequest.amount()));
            wallet.setUsableBalance(wallet.getUsableBalance().subtract(transactionRequest.amount()));
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return transactionResponseConverter.convertToResponse(transaction);
    }

    @Transactional
    public TransactionResponse changeTransactionStatus(final TransactionApprovalRequest transactionApprovalRequest, String token){
        Transaction transaction = transactionRepository.findById(transactionApprovalRequest.transactionId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.TRANSACTION_NOT_FOUND));

        Wallet wallet = transaction.getWallet();
        checkUserAuthorization(wallet.getId(), token);
        if(transaction.getStatus() != TransactionStatus.PENDING) {
            log.error("Transaction with ID {} is not pending", transaction.getId());
            throw new RuntimeException(ErrorMessages.TRANSACTION_IS_NOT_PENDING);
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
            throw new RuntimeException(ErrorMessages.INVALID_TRANSACTION_STATUS);
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
        return transactionResponseConverter.convertToResponse(transaction);
    }

    public void checkUserAuthorization(long walletId, String token) {
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        if(!role.equals(Role.EMPLOYEE.name())){
            Wallet wallet = walletRepository.findById(walletId).get();
            if(wallet == null || !wallet.getCustomer().getUserName().equals(username)) {
                log.error("Unauthorized access attempt by user: {}", username);
                throw new RuntimeException(ErrorMessages.UNAUTHORIZED_ACCESS_TO_THIS_WALLET);
            }
        }
    }
}
