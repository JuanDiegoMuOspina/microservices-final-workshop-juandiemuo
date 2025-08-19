package com.apigateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret = "a-very-long-and-secure-secret-key-for-testing-purposes-only-1234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
    }

    private String generateTestToken(Date expirationDate) {
        return Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    @DisplayName("isValidToken_whenTokenIsValidAndNotExpired_shouldReturnTrue")
    void isValidToken_whenTokenIsValidAndNotExpired_shouldReturnTrue() {
        Date futureExpiration = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        String validToken = generateTestToken(futureExpiration);

        boolean isValid = jwtService.isValidToken(validToken);

        assertTrue(isValid);
    }



    @Test
    @DisplayName("isValidToken_whenTokenHasInvalidSignature_shouldThrowSignatureException")
    void isValidToken_whenTokenHasInvalidSignature_shouldThrowSignatureException() {
        String wrongSecret = "a-completely-different-secret-key-that-will-not-match-the-original-one";
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("testUser")
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(wrongSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(SignatureException.class, () -> {
            jwtService.isValidToken(tokenWithWrongSignature);
        });
    }

    @Test
    @DisplayName("isValidToken_whenTokenIsMalformed_shouldThrowMalformedJwtException")
    void isValidToken_whenTokenIsMalformed_shouldThrowMalformedJwtException() {
        String malformedToken = "this.is.not.a.valid.jwt";

        assertThrows(MalformedJwtException.class, () -> {
            jwtService.isValidToken(malformedToken);
        });
    }
}
