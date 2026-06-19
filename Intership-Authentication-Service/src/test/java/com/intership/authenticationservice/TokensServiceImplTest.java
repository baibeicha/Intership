package com.intership.authenticationservice;

import com.intership.authenticationservice.exception.UserCredentialsNotFoundException;
import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.model.dto.Tokens;
import com.intership.authenticationservice.service.JwtService;
import com.intership.authenticationservice.service.UserCredentialsService;
import com.intership.authenticationservice.service.impl.TokensServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokensServiceImplTest {

    @Mock
    private UserCredentialsService userCredentialsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TokensServiceImpl tokensService;

    private final String USERNAME = "testuser";
    private final String PASSWORD = "password";
    private final String ENCODED_PASSWORD = "encodedPassword";
    private final String ACCESS_TOKEN = "access.token";
    private final String REFRESH_TOKEN = "refresh.token";

    @BeforeEach
    void setUp() {
        tokensService = new TokensServiceImpl(jwtService, passwordEncoder);
        tokensService.setUserCredentialsService(userCredentialsService);
    }

    @Test
    void generateTokens_WhenUserExistsAndPasswordMatches_ShouldReturnTokens() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity storedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);

        when(userCredentialsService.existsByUsername(USERNAME)).thenReturn(true);
        when(userCredentialsService.findByUsername(USERNAME)).thenReturn(storedAuth);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateAccessToken(auth)).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(auth)).thenReturn(REFRESH_TOKEN);

        Tokens result = tokensService.generateTokens(auth);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        verify(userCredentialsService).existsByUsername(USERNAME);
        verify(userCredentialsService).findByUsername(USERNAME);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateAccessToken(auth);
        verify(jwtService).generateRefreshToken(auth);
    }

    @Test
    void generateTokens_WhenUserNotExists_ShouldThrowException() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        when(userCredentialsService.existsByUsername(USERNAME)).thenReturn(false);

        assertThrows(UserCredentialsNotFoundException.class,
                () -> tokensService.generateTokens(auth));
        verify(userCredentialsService).existsByUsername(USERNAME);
        verify(userCredentialsService, never()).findByUsername(anyString());
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void generateTokens_WhenPasswordDoesNotMatch_ShouldThrowSecurityException() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        AuthEntity storedAuth = new AuthEntity(USERNAME, ENCODED_PASSWORD);

        when(userCredentialsService.existsByUsername(USERNAME)).thenReturn(true);
        when(userCredentialsService.findByUsername(USERNAME)).thenReturn(storedAuth);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(SecurityException.class,
                () -> tokensService.generateTokens(auth));
        verify(userCredentialsService).existsByUsername(USERNAME);
        verify(userCredentialsService).findByUsername(USERNAME);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void refreshTokens_ShouldReturnRefreshedTokens() {
        Tokens oldTokens = new Tokens(ACCESS_TOKEN, REFRESH_TOKEN);
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        when(userCredentialsService.findByUsername(ACCESS_TOKEN)).thenReturn(auth);
        String NEW_ACCESS_TOKEN = "new.access.token";
        when(jwtService.refreshAccessToken(ACCESS_TOKEN, auth)).thenReturn(NEW_ACCESS_TOKEN);

        Tokens result = tokensService.refreshTokens(oldTokens);

        assertNotNull(result);
        assertEquals(NEW_ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        verify(userCredentialsService).findByUsername(ACCESS_TOKEN);
        verify(jwtService).refreshAccessToken(ACCESS_TOKEN, auth);
    }

    @Test
    void refreshTokens_WhenJwtServiceThrowsException_ShouldPropagateException() {
        Tokens oldTokens = new Tokens(ACCESS_TOKEN, REFRESH_TOKEN);
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        when(userCredentialsService.findByUsername(ACCESS_TOKEN)).thenReturn(auth);
        when(jwtService.refreshAccessToken(ACCESS_TOKEN, auth))
                .thenThrow(new SecurityException("Invalid refresh token"));

        assertThrows(SecurityException.class,
                () -> tokensService.refreshTokens(oldTokens));
        verify(userCredentialsService).findByUsername(ACCESS_TOKEN);
        verify(jwtService).refreshAccessToken(ACCESS_TOKEN, auth);
    }

    @Test
    void refreshTokens_WhenUserNotFound_ShouldPropagateException() {
        Tokens oldTokens = new Tokens(ACCESS_TOKEN, REFRESH_TOKEN);

        when(userCredentialsService.findByUsername(ACCESS_TOKEN))
                .thenThrow(new UserCredentialsNotFoundException(USERNAME));

        assertThrows(UserCredentialsNotFoundException.class,
                () -> tokensService.refreshTokens(oldTokens));
        verify(userCredentialsService).findByUsername(ACCESS_TOKEN);
        verify(jwtService, never()).refreshAccessToken(anyString(), any());
    }
}