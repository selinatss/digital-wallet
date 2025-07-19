package com.wallet.digitalwallet.model.request;

import com.wallet.digitalwallet.enums.Role;

public record RegisterRequest(
        String userName,
        String password,
        String name,
        String surname,
        String tckn,
        Role role
){
    public RegisterRequest {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be null or blank");
        }
        if (tckn == null || tckn.length() != 11 || !tckn.matches("\\d{11}")) {
            throw new IllegalArgumentException("TCKN cannot be null or blank");
        }
    }
};
