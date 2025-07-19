package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.entity.Wallet;
import com.wallet.digitalwallet.model.request.CreateWalletRequest;
import com.wallet.digitalwallet.model.response.WalletResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import com.wallet.digitalwallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    public WalletResponse createWallet(final CreateWalletRequest createWalletRequest) {
        Customer customer = customerRepository.findByTckn(createWalletRequest.tckn());
        if(customer == null) {
            throw new RuntimeException("Customer not found with TCKN: " + createWalletRequest.tckn());
        }

        Wallet wallet = createWallet(createWalletRequest, customer);

         walletRepository.save(wallet);

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

    public List<WalletResponse> listWallets(String tckn) {
        Customer customer = customerRepository.findByTckn(tckn);
        if (customer == null) {
            throw new RuntimeException("Customer not found with TCKN: " + tckn);
        }
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        if (wallets.isEmpty()) {
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
