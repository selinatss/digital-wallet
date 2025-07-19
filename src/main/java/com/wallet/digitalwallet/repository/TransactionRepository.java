package com.wallet.digitalwallet.repository;

import com.wallet.digitalwallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<List<Transaction>> findByWalletId(long walletId);
}
