package com.example.repo_be_v2.global.security.jwt;

import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {
    @Test
    void 생성한_토큰에서_학번과_권한을_읽을_수_있다() {
        JwtTokenProvider provider = new JwtTokenProvider(
                "test-secret-key-that-is-longer-than-32-bytes",
                3_600_000
        );
        provider.initializeKey();

        TokenResponse response = provider.createToken("20260001", Role.STUDENT);
        Claims claims = provider.parseClaims(response.accessToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(claims.getSubject()).isEqualTo("20260001");
        assertThat(claims.get("role", String.class)).isEqualTo("STUDENT");
    }
}
