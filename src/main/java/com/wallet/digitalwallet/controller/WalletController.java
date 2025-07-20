package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(@RequestBody final CreateWalletRequest createWalletRequest, @RequestHeader("Authorization") final String token) {
            return new ResponseEntity<>(walletService.createWallet(createWalletRequest, token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/list/{customerId}")
    public ResponseEntity<List<WalletResponse>> listWallets(@PathVariable final long customerId, @RequestHeader("Authorization") final String token) {
            return new ResponseEntity<>(walletService.listWallets(customerId, token.substring(7)), HttpStatus.OK);
    }
}
