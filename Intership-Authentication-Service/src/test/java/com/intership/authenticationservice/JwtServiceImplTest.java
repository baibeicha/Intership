package com.intership.authenticationservice;

import com.intership.authenticationservice.model.dto.AuthEntity;
import com.intership.authenticationservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    private final String USERNAME = "testuser";
    private final String PASSWORD = "password";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtServiceImpl();

        String SECRET_KEY = "mySecretKeymySecretKeymySecretKeymySecretKey";
        setField(jwtService, "secretKey", SECRET_KEY);
        long ACCESS_TOKEN_EXPIRATION = 3600000L;
        setField(jwtService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        long REFRESH_TOKEN_EXPIRATION = 86400000L;
        setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        String token = jwtService.generateAccessToken(auth);

        String result = jwtService.extractUsername(token);

        assertEquals(USERNAME, result);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        String token = jwtService.generateAccessToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(USERNAME, jwtService.extractUsername(token));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        String token = jwtService.generateRefreshToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(USERNAME, jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        String token = jwtService.generateAccessToken(auth);

        boolean result = jwtService.isTokenValid(token);

        assertTrue(result);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() throws Exception {
        setField(jwtService, "accessTokenExpiration", -1000L);
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        String token = jwtService.generateAccessToken(auth);

        boolean result = jwtService.isTokenValid(token);

        assertFalse(result);
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid_token";

        boolean result = jwtService.isTokenValid(invalidToken);

        assertFalse(result);
    }

    @Test
    void refreshAccessToken_WithValidRefreshToken_ShouldReturnNewAccessToken() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        String refreshToken = jwtService.generateRefreshToken(auth);

        String newAccessToken = jwtService.refreshAccessToken(refreshToken, auth);

        assertNotNull(newAccessToken);
        assertFalse(newAccessToken.isEmpty());
        assertEquals(USERNAME, jwtService.extractUsername(newAccessToken));
        assertNotEquals(refreshToken, newAccessToken);
    }

    @Test
    void refreshAccessToken_WithInvalidRefreshToken_ShouldThrowException() {
        String differentSecret = "aDifferentSecretKeyaDifferentSecretKeyaDifferentSecretKey";
        SecretKey wrongKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String invalidToken = Jwts.builder()
                .subject(USERNAME)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5000L))
                .signWith(wrongKey)
                .compact();

        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);

        assertThrows(SecurityException.class,
                () -> jwtService.refreshAccessToken(invalidToken, auth));
    }

    @Test
    void extractAllClaims_ShouldReturnClaims() {
        AuthEntity auth = new AuthEntity(USERNAME, PASSWORD);
        String token = jwtService.generateAccessToken(auth);

        Claims claims = extractAllClaimsUsingReflection(token);

        assertNotNull(claims);
        assertEquals(USERNAME, claims.getSubject());
    }

    private Claims extractAllClaimsUsingReflection(String token) {
        try {
            var method = JwtServiceImpl.class.getDeclaredMethod("extractAllClaims", String.class);
            method.setAccessible(true);
            return (Claims) method.invoke(jwtService, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSigningKey_ShouldReturnSecretKey() throws Exception {
        var method = JwtServiceImpl.class.getDeclaredMethod("getSigningKey");
        method.setAccessible(true);
        SecretKey key = (SecretKey) method.invoke(jwtService);

        assertNotNull(key);
    }
}