package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.model.request.TransactionApprovalRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
import com.wallet.digitalwallet.model.response.TransactionResponse;
import com.wallet.digitalwallet.service.JwtService;
import com.wallet.digitalwallet.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final JwtService jwtService;

    @GetMapping("/list/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable final long walletId, @RequestHeader("Authorization") final String token) {
        return new ResponseEntity<>(transactionService.getTransactionsByWalletId(walletId, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody final TransactionRequest transactionRequest, @RequestHeader("Authorization") final String token){
        return ResponseEntity.ok(transactionService.deposit(transactionRequest, token.substring(7)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody final TransactionRequest transactionRequest, @RequestHeader("Authorization") final String token){
        return ResponseEntity.ok(transactionService.withdraw(transactionRequest, token.substring(7)));
    }

    @PostMapping("/status")
    public ResponseEntity<TransactionResponse> approveTransaction(@RequestBody final TransactionApprovalRequest transactionApprovalRequest, @RequestHeader("Authorization") final String token) {
         return ResponseEntity.ok(transactionService.changeTransactionStatus(transactionApprovalRequest, token.substring(7)));
    }

}
