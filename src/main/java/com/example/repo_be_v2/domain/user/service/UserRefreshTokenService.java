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

    //기업은 테이블이 달라 PK가 사용자와 겹친다. 같은 키에 덮어쓰지 않게 자리를 나눈다.
    private static final String COMPANY_REFRESH_TOKEN_KEY_PREFIX = "refresh-token:company:";

    private final StringRedisTemplate redisTemplate;

    @org.springframework.beans.factory.annotation.Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void save(Long userId, String refreshToken) {
        save(REFRESH_TOKEN_KEY_PREFIX + userId, refreshToken);
    }

    public void saveForCompany(Long companyId, String refreshToken) {
        save(COMPANY_REFRESH_TOKEN_KEY_PREFIX + companyId, refreshToken);
    }

    public boolean matchesForCompany(Long companyId, String refreshToken) {
        return matches(COMPANY_REFRESH_TOKEN_KEY_PREFIX + companyId, refreshToken);
    }

    //기업 계정을 삭제할 때 재발급 길도 함께 끊는다.
    public void deleteForCompany(Long companyId) {
        redisTemplate.delete(COMPANY_REFRESH_TOKEN_KEY_PREFIX + companyId);
    }

    private void save(String key, String refreshToken) {
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );
    }

    public boolean matches(Long userId, String refreshToken) {
        return matches(REFRESH_TOKEN_KEY_PREFIX + userId, refreshToken);
    }

    private boolean matches(String key, String refreshToken) {
        String savedRefreshToken = redisTemplate.opsForValue()
                .get(key);

        if (savedRefreshToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                savedRefreshToken.getBytes(StandardCharsets.UTF_8),
                refreshToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
