package com.example.repo_be_v2.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserRefreshTokenService {
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;

    @org.springframework.beans.factory.annotation.Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_KEY_PREFIX + userId,
                refreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );
    }

    public boolean matches(Long userId, String refreshToken) {
        String savedRefreshToken = redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_KEY_PREFIX + userId);

        if (savedRefreshToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                savedRefreshToken.getBytes(StandardCharsets.UTF_8),
                refreshToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
