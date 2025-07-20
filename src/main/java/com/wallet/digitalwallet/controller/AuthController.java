package com.wallet.digitalwallet.controller;

import com.wallet.digitalwallet.model.response.RegisterResponse;
import com.wallet.digitalwallet.service.JwtService;
import com.wallet.digitalwallet.model.request.AuthRequest;
import com.wallet.digitalwallet.model.request.RegisterRequest;
import com.wallet.digitalwallet.model.response.AuthResponse;
import com.wallet.digitalwallet.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody final RegisterRequest request) {
        return new ResponseEntity<>(userDetailsService.registerUser(request), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody final AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.userName());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
