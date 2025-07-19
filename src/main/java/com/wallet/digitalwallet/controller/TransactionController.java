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
        try {
            return new ResponseEntity<>(transactionService.getTransactionsByWalletId(walletId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Handle error appropriately
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody final TransactionRequest transactionRequest){
        try {
            return ResponseEntity.ok(transactionService.deposit(transactionRequest));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody final TransactionRequest transactionRequest){
        try {
            return ResponseEntity.ok(transactionService.withdraw(transactionRequest));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/status")
    public ResponseEntity<TransactionResponse> approveTransaction(@RequestBody final TransactionApprovalRequest transactionApprovalRequest) {
        try {
            return ResponseEntity.ok(transactionService.approveTransaction(transactionApprovalRequest));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
