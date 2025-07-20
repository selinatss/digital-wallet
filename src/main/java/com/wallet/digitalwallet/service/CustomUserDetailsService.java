package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.model.request.RegisterRequest;
import com.wallet.digitalwallet.model.response.RegisterResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;


    public RegisterResponse registerUser(final RegisterRequest request) {
        if (customerRepository.findByTckn(request.tckn()) != null || customerRepository.findByUserName(request.userName()) != null) {
            throw new RuntimeException("User already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setSurname(request.surname());
        customer.setTckn(request.tckn());
        customer.setUserName(request.userName());
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setRole(request.role());

        customerRepository.save(customer);
        return new RegisterResponse(customer.getUserName(), customer.getName(), customer.getSurname(), customer.getRole(), "successfully registered");
    }



    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUserName(username);
        return new org.springframework.security.core.userdetails.User(
                customer.getUserName(),
                customer.getPassword(),
                List.of(new SimpleGrantedAuthority(customer.getRole().name()) // rol bilgisi ekle
        ));
    }
}