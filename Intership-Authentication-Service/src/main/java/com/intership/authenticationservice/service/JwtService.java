package com.intership.authenticationservice.service;

import com.intership.authenticationservice.model.dto.AuthEntity;

public interface JwtService {
    String extractUsername(String token);
    String generateAccessToken(AuthEntity auth);
    String generateRefreshToken(AuthEntity auth);
    boolean isTokenValid(String token);
    String refreshAccessToken(String refreshToken, AuthEntity auth);
}
