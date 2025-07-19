package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(@RequestBody final CreateWalletRequest createWalletRequest) {
        try {
            return new ResponseEntity<>(walletService.createWallet(createWalletRequest), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<WalletResponse>> listWallets(@PathVariable String tckn) {
        try {
            return new ResponseEntity<>(walletService.listWallets(tckn), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
