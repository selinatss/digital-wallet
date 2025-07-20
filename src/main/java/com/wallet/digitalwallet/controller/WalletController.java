package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.request.ListWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(@RequestBody final CreateWalletRequest createWalletRequest, @RequestHeader("Authorization") final String token) {
            return new ResponseEntity<>(walletService.createWallet(createWalletRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<List<WalletResponse>> listWallets(@RequestBody final ListWalletRequest listWalletRequest, @RequestHeader("Authorization") final String token) {
            return new ResponseEntity<>(walletService.listWallets(listWalletRequest.tckn(), token.substring(7)), HttpStatus.OK);
    }
}
