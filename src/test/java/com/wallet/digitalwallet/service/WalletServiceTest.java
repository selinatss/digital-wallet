package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Currency;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.utils.WalletResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private WalletResponseConverter walletResponseConverter;

    @InjectMocks
    private WalletService walletService;

    String token = "token";

    private Customer mockCustomer(String username, Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUserName(username);
        customer.setTckn("12345678901");
        return customer;
    }

    private Wallet mockWallet(Customer customer) {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletName("Main");
        wallet.setCurrency(Currency.TRY);
        wallet.setActiveForShopping(true);
        wallet.setActiveForWithdraw(true);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(900));
        wallet.setCustomer(customer);
        return wallet;
    }

    @Test
    void createWalletShouldSucceedWhenEmployeeCreatesWallet() {
        String username = "employee1";

        CreateWalletRequest request = new CreateWalletRequest("MyWallet", Currency.TRY, true, true, 1234L);
        Customer customer = new Customer();
        customer.setId(1234L);
        customer.setUserName("someCustomer");

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());
        when(customerRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response = walletService.createWallet(request, token);

        assertEquals("MyWallet", response.walletName());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWalletShouldThrowWhenCustomerTriesToCreateOthersWallet() {

        CreateWalletRequest request = new CreateWalletRequest("WalletX", Currency.TRY, false, false, 12345L);
        Customer customer = new Customer();
        customer.setId(1234L);
        customer.setUserName("targetUser");

        when(jwtService.extractUsername(token)).thenReturn("otherCustomer");
        when(jwtService.extractRole(token)).thenReturn(Role.CUSTOMER.name());
        when(customerRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        assertThrows(RuntimeException.class, () -> walletService.createWallet(request, token));
    }

    @Test
    void createWalletShouldThrowWhenCustomerNotFound() {
        CreateWalletRequest request = new CreateWalletRequest("wallet", Currency.EUR, true, false, 12345L);
        when(customerRepository.findById(request.customerId())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> walletService.createWallet(request, token));
    }

    @Test
    void listWalletShouldReturnWalletsWhenEmployeeRequestsOtherCustomer() {
        Customer customer = mockCustomer("customerUser", 1L);
        Wallet wallet = mockWallet(customer);

        when(jwtService.extractUsername(token)).thenReturn("employeeUser");
        when(jwtService.extractRole(token)).thenReturn(Role.EMPLOYEE.name());
        when(customerRepository.findById(1234L)).thenReturn(Optional.of(customer));
        when(walletRepository.findByCustomerId(1L)).thenReturn(List.of(wallet));
        List<WalletResponse> result = walletService.listWallets(1234L, token);

        assertEquals(1, result.size());
        assertEquals(wallet.getId(), result.get(0).walletId());
    }

    @Test
    void listWalletsShouldReturnWalletsWhenCustomerRequestsOwnWallet() {
        Customer customer = mockCustomer("customerUser", 1L);
        Wallet wallet = mockWallet(customer);

        when(jwtService.extractUsername(token)).thenReturn("customerUser");
        when(jwtService.extractRole(token)).thenReturn("CUSTOMER");
        when(customerRepository.findById(1234L)).thenReturn(Optional.of(customer));
        when(walletRepository.findByCustomerId(1L)).thenReturn(List.of(wallet));

        List<WalletResponse> result = walletService.listWallets(1234L, token);

        assertEquals(1, result.size());
        assertEquals(wallet.getWalletName(), result.get(0).walletName());
    }

    @Test
    void listWalletsShouldThrowWhenCustomerTriesToAccessOthersWallet() {
        Customer customer = mockCustomer("otherUser", 1L);

        when(jwtService.extractUsername(token)).thenReturn("customerUser");
        when(jwtService.extractRole(token)).thenReturn("CUSTOMER");
        when(customerRepository.findById(1234L)).thenReturn(Optional.of(customer));

        assertThrows(RuntimeException.class,
                () -> walletService.listWallets(1234L, token));
    }

    @Test
    void listWalletShouldThrowWhenCustomerNotFound() {

        when(customerRepository.findById(1234L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> walletService.listWallets(1234L, token));
    }

    @Test
    void listWalletsShouldThrowWhenNoWalletsFound() {
        Customer customer = mockCustomer("customerUser", 1L);

        when(jwtService.extractUsername(token)).thenReturn("customerUser");
        when(jwtService.extractRole(token)).thenReturn("CUSTOMER");
        when(customerRepository.findById(1234L)).thenReturn(Optional.of(customer));
        when(walletRepository.findByCustomerId(1L)).thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> walletService.listWallets(1234L, token));
    }
}
