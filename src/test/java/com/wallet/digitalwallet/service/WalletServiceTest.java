package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Currency;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private WalletService walletService;

    String token = "token";

    @Test
    void createWalletShouldSaveWalletWithInitialBalances() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, "12345678901");
        Customer customer = new Customer();
        Wallet wallet = Wallet.builder()
                .walletName(request.walletName())
                .currency(request.currency())
                .customer(customer)
                .activeForShopping(request.activeForShopping())
                .activeForWithdraw(request.activeForWithdrawal())
                .balance(BigDecimal.ZERO)
                .usableBalance(BigDecimal.ZERO)
                .build();
        when(customerRepository.findByTckn(request.tckn())).thenReturn(customer);
        when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(wallet);

        WalletResponse createdWallet = walletService.createWallet(request, token);

        assertEquals("Test Wallet", createdWallet.walletName());
        assertEquals(Currency.USD, createdWallet.currency());
        assertEquals(BigDecimal.ZERO, createdWallet.balance());
        assertEquals(BigDecimal.ZERO, createdWallet.usableBalance());
        assertTrue(createdWallet.activeForShopping());
        assertTrue(createdWallet.activeForWithdrawal());
    }

    @Test
    void createWalletShouldThrowExceptionWhenCustomerNotFound() {
        CreateWalletRequest request = new CreateWalletRequest("Test Wallet", Currency.USD, true, true, "12345678901");
        when(customerRepository.findByTckn(request.tckn())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> walletService.createWallet(request, token));
    }

    @Test
    void listWalletsShouldReturnWalletResponsesWhenCustomerAndWalletsExist() {
        String tckn = "12345678901";
        Customer customer = new Customer();
        customer.setId(1L);
        Wallet wallet = new Wallet();
        wallet.setCustomer(customer);
        List<Wallet> wallets = List.of(wallet);

        when(customerRepository.findByTckn(tckn)).thenReturn(customer);
        when(walletRepository.findByCustomerId(customer.getId())).thenReturn(wallets);

        List<WalletResponse> result = walletService.listWallets(tckn, token);

        assertEquals(1, result.size());
    }

    @Test
    void listWalletsShouldThrowExceptionWhenCustomerDoesNotExist() {
        String tckn = "12345678901";

        when(customerRepository.findByTckn(tckn)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> walletService.listWallets(tckn, token));
    }

    @Test
    void listWalletsShouldThrowExceptionWhenNoWalletsExistForCustomer() {
        String tckn = "12345678901";
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findByTckn(tckn)).thenReturn(customer);
        when(walletRepository.findByCustomerId(customer.getId())).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> walletService.listWallets(tckn, token));
    }
}