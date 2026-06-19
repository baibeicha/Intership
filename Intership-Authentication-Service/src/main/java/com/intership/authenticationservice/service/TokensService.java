package com.intership.authenticationservice.service;

import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.Tokens;

public interface TokensService {
    Tokens generateTokens(AuthEntity auth);
    Tokens refreshTokens(Tokens tokens);
}
