package com.intership.authenticationservice.controller;

import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.Tokens;
import com.intership.authenticationservice.model.dto.UserDto;
import com.intership.authenticationservice.model.dto.UserRegistrationRequest;
import com.intership.authenticationservice.service.JwtService;
import com.intership.authenticationservice.service.TokensService;
import com.intership.authenticationservice.service.UserCredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserCredentialsService userCredentialsService;
    private final TokensService tokensService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userCredentialsService.register(request));
    }

    @PostMapping("/tokens")
    public ResponseEntity<Tokens> tokens(@RequestBody AuthEntity authEntity) {
        return ResponseEntity.ok(tokensService.generateTokens(authEntity));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody String token) {
        return ResponseEntity.ok(jwtService.isTokenValid(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(@RequestBody Tokens tokens) {
        return ResponseEntity.ok(tokensService.refreshTokens(tokens));
    }
}
