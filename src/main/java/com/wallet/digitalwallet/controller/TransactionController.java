package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable final long walletId) {
         return new ResponseEntity<>(transactionService.getTransactionsByWalletId(walletId), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody final TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.deposit(transactionRequest));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody final TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.withdraw(transactionRequest));
    }

    @PostMapping("/status")
    public ResponseEntity<TransactionResponse> approveTransaction(@RequestBody final TransactionApprovalRequest transactionApprovalRequest) {
         return ResponseEntity.ok(transactionService.approveTransaction(transactionApprovalRequest));
    }
}
