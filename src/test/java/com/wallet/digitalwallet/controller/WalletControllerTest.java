package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.enums.Currency;
import com.wallet.digitalwallet.enums.PartyType;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.request.ListWalletRequest;
import com.wallet.digitalwallet.model.request.TransactionRequest;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    String token = "Bearer testToken";
    @Test
    void createWalletShouldReturnWalletWhenRequestIsValid() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, 1224L);
        WalletResponse walletResponse = new WalletResponse(1l, "walletName", Currency.USD, true, true, new BigDecimal(0.0), new BigDecimal(0.0), 1234l);

        when(walletService.createWallet(request, "testToken")).thenReturn(walletResponse);

        ResponseEntity<WalletResponse> response = walletController.createWallet(request, token);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(walletResponse, response.getBody());
    }

    @Test
    void createWalletShouldReturnInternalServerErrorWhenExceptionOccurs() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, 1234L);

        when(walletService.createWallet(request, "testToken")).thenThrow(new RuntimeException());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> walletController.createWallet(request, token)
        );
    }

    @Test
    void listWalletsShouldReturnListOfWalletsWhenServiceReturnsData() {
        List<WalletResponse> wallets = List.of(new WalletResponse(1234L, "Test Wallet", Currency.USD, true, true, new BigDecimal(100.0), new BigDecimal(100.0), 1234l));

        when(walletService.listWallets(1234L, "testToken")).thenReturn(wallets);

        ResponseEntity<List<WalletResponse>> response = walletController.listWallets(1234L, token);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(wallets, response.getBody());
    }


    @Test
    void listWalletsShouldReturnInternalServerErrorWhenExceptionOccurs() {
        TransactionRequest request = new TransactionRequest(1234L, new BigDecimal(1000.0), PartyType.IBAN);

        when(walletService.listWallets(1234L, "testToken"))
                .thenThrow(new RuntimeException());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> walletController.listWallets(1234L, token)
        );
    }
}