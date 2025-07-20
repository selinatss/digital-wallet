package com.wallet.digitalwallet.service;

import com.wallet.digitalwallet.entity.Customer;
import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.model.request.RegisterRequest;
import com.wallet.digitalwallet.model.response.RegisterResponse;
import com.wallet.digitalwallet.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void registerUserShouldThrowExceptionWhenUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest("newUser", "password", "name", "surname", "12345678901", Role.CUSTOMER);

        when(customerRepository.findByUserName(request.userName())).thenReturn(new Customer());

        assertThrows(RuntimeException.class, () -> customUserDetailsService.registerUser(request));
    }

    @Test
    void registerUserShouldSaveCustomerWhenRequestIsValid() {
        RegisterRequest request = new RegisterRequest("newUser", "password", "name", "surname", "12345678901", Role.CUSTOMER);

        when(customerRepository.findByTckn(request.tckn())).thenReturn(null);
        when(customerRepository.findByUserName(request.userName())).thenReturn(null);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        RegisterResponse response = customUserDetailsService.registerUser(request);

        verify(customerRepository, times(1)).save(any(Customer.class));
        assertEquals("newUser", response.userName());
        assertEquals("name", response.name());
        assertEquals("surname", response.surname());
        assertEquals(Role.CUSTOMER, response.role());
        assertEquals("successfully registered", response.message());
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        Customer customer = new Customer();
        customer.setUserName("existingUser");
        customer.setPassword("encodedPassword");
        customer.setRole(Role.CUSTOMER);

        when(customerRepository.findByUserName("existingUser")).thenReturn(customer);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("existingUser");

        assertEquals("existingUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.CUSTOMER.name())));
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionWhenUserDoesNotExist() {
        when(customerRepository.findByUserName("nonExistentUser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("nonExistentUser"));
    }
}