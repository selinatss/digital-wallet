package com.wallet.digitalwallet.repository;

import com.wallet.digitalwallet.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByTckn(String tckn);
    Customer findByUserName(String userName);
}
