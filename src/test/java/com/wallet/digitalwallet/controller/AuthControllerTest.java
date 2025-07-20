package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.enums.Role;
import com.wallet.digitalwallet.model.request.AuthRequest;
import com.wallet.digitalwallet.model.request.RegisterRequest;
import com.wallet.digitalwallet.model.response.AuthResponse;
import com.wallet.digitalwallet.model.response.RegisterResponse;
import com.wallet.digitalwallet.service.CustomUserDetailsService;
import com.wallet.digitalwallet.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUserShouldReturnOkWhenRequestIsValid() {
        RegisterRequest request = new RegisterRequest("user", "password", "name", "surname", "12345678901", Role.CUSTOMER);
        RegisterResponse expectedResponse = new RegisterResponse(1l,"user", "name", "surname", Role.CUSTOMER, "User registered successfully");

        when(userDetailsService.registerUser(request)).thenReturn(expectedResponse);

        ResponseEntity<RegisterResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void registerUserShouldThrowRuntimeExceptionWhenServiceThrowsException() {
        RegisterRequest request = new RegisterRequest("user", "password", "name", "surname", "12345678901", Role.CUSTOMER);
        RegisterResponse expectedResponse = new RegisterResponse(1l,"user", "name", "surname", Role.CUSTOMER, "User registered successfully");

        when(userDetailsService.registerUser(request)).thenThrow(new RuntimeException("Registration failed"));

        assertThrows(RuntimeException.class, () -> authController.registerUser(request));
    }

    @Test
    void authenticateShouldReturnJwtWhenCredentialsAreValid() {
        AuthRequest request = new AuthRequest("user", "password");
        UserDetails userDetails = mock(UserDetails.class);
        String jwt = "valid.jwt.token";

        when(userDetailsService.loadUserByUsername(request.userName())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(jwt);

        ResponseEntity<?> response = authController.authenticate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwt, ((AuthResponse) response.getBody()).token());
    }

    @Test
    void authenticateShouldThrowExceptionWhenCredentialsAreInvalid() {
        AuthRequest request = new AuthRequest("user", "wrongPassword");

        doThrow(new RuntimeException("Authentication failed")).when(authenticationManager)
                .authenticate(new UsernamePasswordAuthenticationToken(request.userName(), request.password()));

        assertThrows(RuntimeException.class, () -> authController.authenticate(request));
    }
}
