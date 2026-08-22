package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.exception.EmailNotVerifiedException;
import com.example.repo_be_v2.domain.user.exception.EmailVerificationCodeExpiredException;
import com.example.repo_be_v2.domain.user.exception.EmailVerificationCodeMismatchException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserEmailVerifyService {
    private static final String CODE_KEY_PREFIX = "email-verification:code:";
    private static final String VERIFIED_KEY_PREFIX = "email-verification:verified:";
    private static final String COOLDOWN_KEY_PREFIX = "email-verification:cooldown:";
    private static final Duration VERIFIED_EXPIRATION = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    public void execute(EmailVerificationConfirmRequest request) {
        String email = request.email();
        String codeKey = CODE_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(codeKey);

        if (savedCode == null) {
            throw new EmailVerificationCodeExpiredException();
        }

        if (!codesMatch(savedCode, request.code())) {
            throw new EmailVerificationCodeMismatchException();
        }
        redisTemplate.delete(codeKey);
        redisTemplate.opsForValue().set(
                VERIFIED_KEY_PREFIX + email,
                "true",
                VERIFIED_EXPIRATION
        );

    }
    public void validateVerified(String email) { //인증된 이메일인지 판단하는 확인하는 코드
        if (!Boolean.TRUE.equals(
                redisTemplate.hasKey(VERIFIED_KEY_PREFIX + email)
        )) {
            throw new EmailNotVerifiedException();
        }
    }

    public void clearVerification(String email) { //인증이 끝난 회원가입후 redis저장소에서 제거;
        redisTemplate.delete(VERIFIED_KEY_PREFIX + email);
        redisTemplate.delete(COOLDOWN_KEY_PREFIX + email);
    }

    private boolean codesMatch(String savedCode, String inputCode) { //인증코드 비교
        return MessageDigest.isEqual(
                savedCode.getBytes(StandardCharsets.UTF_8),
                inputCode.getBytes(StandardCharsets.UTF_8)
        );
    }
}
