package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;

    public WalletResponse createWallet(final CreateWalletRequest createWalletRequest, String token) {
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        Customer customer = customerRepository.findByTckn(createWalletRequest.tckn());
        if(customer == null) {
            log.error("Customer not found with TCKN: {}", createWalletRequest.tckn());
            throw new RuntimeException("Customer not found with TCKN: " + createWalletRequest.tckn());
        }
        if(!role.equals(Role.EMPLOYEE)){
            if(!customer.getUserName().equals(username)) {
                log.error("Unauthorized access to wallet creation for customer with username: {}", customer.getUserName());
                throw new RuntimeException("Unauthorized access to this wallet");
            }
        }

        Wallet wallet = createWallet(createWalletRequest, customer);

        walletRepository.save(wallet);
        log.info("Wallet created successfully for customer with TCKN: {}", createWalletRequest.tckn());
        return new WalletResponse(
                wallet.getId(),
                wallet.getWalletName(),
                wallet.getCurrency(),
                wallet.isActiveForShopping(),
                wallet.isActiveForWithdraw(),
                wallet.getBalance(),
                wallet.getUsableBalance(),
                wallet.getCustomer().getTckn()
        );
    }

    public List<WalletResponse> listWallets(String tckn, String token) {
        Customer customer = customerRepository.findByTckn(tckn);
        if (customer == null) {
            log.error("Customer not found with TCKN: {}", tckn);
            throw new RuntimeException("Customer not found with TCKN: " + tckn);
        }
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        if(!role.equals(Role.EMPLOYEE)){
            if(!customer.getUserName().equals(username)) {
                log.error("Unauthorized access to wallet creation for customer with username: {}", customer.getUserName());
                throw new RuntimeException("Unauthorized access to this wallet");
            }
        }
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        if (wallets.isEmpty()) {
            log.error("No wallets found for customer with TCKN: {}", tckn);
            throw new RuntimeException("No wallets found for customer with TCKN: " + tckn);
        }
        return wallets.stream()
                .map(wallet -> new WalletResponse(
                        wallet.getId(),
                        wallet.getWalletName(),
                        wallet.getCurrency(),
                        wallet.isActiveForShopping(),
                        wallet.isActiveForWithdraw(),
                        wallet.getBalance(),
                        wallet.getUsableBalance(),
                        wallet.getCustomer().getTckn()))
                .toList();
    }

    private static Wallet createWallet(CreateWalletRequest createWalletRequest, Customer customer) {
        return Wallet.builder().walletName(createWalletRequest.walletName())
                .currency(createWalletRequest.currency())
                .customer(customer)
                .activeForShopping(createWalletRequest.activeForShopping())
                .activeForWithdraw(createWalletRequest.activeForWithdrawal())
                .balance(BigDecimal.valueOf(0))
                .usableBalance(BigDecimal.valueOf(0))
                .build();
    }
}
