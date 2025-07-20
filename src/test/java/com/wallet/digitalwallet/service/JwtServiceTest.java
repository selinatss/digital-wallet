package com.wallet.digitalwallet.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateTokenAndExtractClaims_ShouldReturnCorrectUsernameAndRole() {
        User userDetails = new User(
                "username",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        assertEquals("username", username);
        assertEquals("CUSTOMER", role);
    }

    @Test
    void extractAllClaims_ShouldContainRoleAndSubject() {
        User userDetails = new User(
                "userName",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );

        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("userName", claims.getSubject());
        assertEquals("EMPLOYEE", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}