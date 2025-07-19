package com.wallet.digitalwallet.model.response;

import com.wallet.digitalwallet.enums.Role;

public record RegisterResponse(String userName, String name, String surname, Role role, String message) {
}
