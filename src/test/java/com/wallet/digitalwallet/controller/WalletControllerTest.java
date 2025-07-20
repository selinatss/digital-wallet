package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.controller.WalletController;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Currency;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.request.ListWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJzdWIiOiJzZWxpbmF0aXMiLCJpYXQiOjE3NTMwMDIxMTgsImV4cCI6MTc1MzA4ODUxOH0.RmZFmMobpS5CnDNRclWGjOMdYnf0v-iEMwpl04-jqus";

    @Test
    void createWalletShouldReturnWalletWhenRequestIsValid() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, "12345678901");
        WalletResponse walletResponse = new WalletResponse(1l, "walletName", Currency.USD, true, true, new BigDecimal(0.0), new BigDecimal(0.0), "12345678901");

        when(walletService.createWallet(request, token)).thenReturn(walletResponse);

        ResponseEntity<WalletResponse> response = walletController.createWallet(request, token);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(walletResponse, response.getBody());
    }

    @Test
    void createWalletShouldReturnInternalServerErrorWhenExceptionOccurs() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, "12345678901");

        when(walletService.createWallet(request, token)).thenThrow(new RuntimeException());

        ResponseEntity<WalletResponse> response = walletController.createWallet(request, token);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void listWalletsShouldReturnListOfWalletsWhenServiceReturnsData() {
        List<WalletResponse> wallets = List.of(new WalletResponse(1234L, "Test Wallet", Currency.USD, true, true, new BigDecimal(100.0), new BigDecimal(100.0), "12345678901"));

        when(walletService.listWallets("1233445556", token)).thenReturn(wallets);

        ResponseEntity<List<WalletResponse>> response = walletController.listWallets(new ListWalletRequest("12345678901"), token);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(wallets, response.getBody());
    }


    @Test
    void listWalletsShouldReturnInternalServerErrorWhenExceptionOccurs() {
        when(walletService.listWallets("1233445556", token)).thenThrow(new RuntimeException());

        ResponseEntity<List<WalletResponse>> response = walletController.listWallets(new ListWalletRequest("12345678901"), token);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}