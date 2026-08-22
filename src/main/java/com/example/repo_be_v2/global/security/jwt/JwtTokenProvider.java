package com.example.repo_be_v2.global.security.jwt;

import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secret;
    private final long accessTokenExpiration;
    private SecretKey key;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @PostConstruct
    void initializeKey() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

<<<<<<< HEAD
    public TokenResponse createToken(String studentEmail, Role role) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);
        String token = Jwts.builder()
                .subject(studentEmail)
=======
    public TokenResponse createToken(String studentId, Role role) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);
        String token = Jwts.builder()
                .subject(studentId)
>>>>>>> origin/1-feat-user-login
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();

        return new TokenResponse("Bearer", token, accessTokenExpiration / 1000);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
