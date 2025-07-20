package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import com.wallet.digitalwallet.utils.ErrorMessages;
import com.wallet.digitalwallet.utils.WalletResponseConverter;
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
    private final WalletResponseConverter walletToWalletResponseConverter;
    private final JwtService jwtService;

    public WalletResponse createWallet(final CreateWalletRequest createWalletRequest, String token) {
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        Customer customer = customerRepository.findById(createWalletRequest.customerId()).get();
        if(customer == null) {
            log.error("Customer not found with customerId: {}", createWalletRequest.customerId());
            throw new RuntimeException(ErrorMessages.CUSTOMER_NOT_FOUND);
        }
        if(!role.equals(Role.EMPLOYEE.name())){
            if(!customer.getUserName().equals(username)) {
                log.error("Unauthorized access to wallet creation for customer with username: {}", customer.getUserName());
                throw new RuntimeException(ErrorMessages.UNAUTHORIZED_ACCESS_TO_THIS_CUSTOMER);
            }
        }

        Wallet wallet = createWallet(createWalletRequest, customer);

        walletRepository.save(wallet);
        log.info("Wallet created successfully for customer{}", createWalletRequest.customerId());
        return walletToWalletResponseConverter.convert(wallet);
    }

    public List<WalletResponse> listWallets(Long customerId, String token) {
        Customer customer = customerRepository.findById(customerId).get();
        if (customer == null) {
            log.error("Customer not found with customerId: {}", customerId);
            throw new RuntimeException(ErrorMessages.CUSTOMER_NOT_FOUND);
        }
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        if(!role.equals(Role.EMPLOYEE.name())) {
            if(!customer.getUserName().equals(username)) {
                log.error("Unauthorized access to wallet creation for customer with username: {}", customer.getUserName());
                throw new RuntimeException(ErrorMessages.UNAUTHORIZED_ACCESS_TO_THIS_CUSTOMER);
            }
        }
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        if (wallets.isEmpty()) {
            log.error("No wallets found for customerId: {}", customerId);
            throw new RuntimeException(ErrorMessages.NO_WALLETS_FOUND_FOR_CUSTOMER);
        }
        return walletToWalletResponseConverter.convertToResponseList(wallets);
    }

    private Wallet createWallet(CreateWalletRequest createWalletRequest, Customer customer) {
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
