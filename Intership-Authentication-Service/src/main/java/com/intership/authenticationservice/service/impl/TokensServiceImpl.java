package com.intership.authenticationservice.service.impl;

import com.intership.authenticationservice.exception.UserCredentialsNotFoundException;
import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.Tokens;
import com.intership.authenticationservice.service.JwtService;
import com.intership.authenticationservice.service.TokensService;
import com.intership.authenticationservice.service.UserCredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokensServiceImpl implements TokensService {

    private UserCredentialsService userCredentialsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserCredentialsService(@Lazy UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }

    @Override
    public Tokens generateTokens(AuthEntity auth) {
        if (!userCredentialsService.existsByUsername(auth.getUsername())) {
            throw new UserCredentialsNotFoundException(auth.getUsername());
        }

        AuthEntity exists = userCredentialsService.findByUsername(auth.getUsername());
        if (!passwordEncoder.matches(auth.getPassword(), exists.getPassword())) {
            throw new SecurityException("Wrong password");
        }

        String accessToken = jwtService.generateAccessToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);
        return new Tokens(accessToken, refreshToken);
    }

    @Override
    public Tokens refreshTokens(Tokens tokens) {
        AuthEntity auth = userCredentialsService.findByUsername(tokens.getAccessToken());
        String newAccessToken = jwtService.refreshAccessToken(tokens.getAccessToken(), auth);
        tokens.setAccessToken(newAccessToken);
        return tokens;
    }
}
