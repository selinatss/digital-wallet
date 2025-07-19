package com.wallet.digitalwallet.model.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank(message = "Username is required") String userName,
                          @NotBlank(message = "Password is required") String password) {
}
