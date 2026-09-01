package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.InvalidRefreshTokenException;
import com.example.repo_be_v2.domain.user.presentation.dto.response.AccessTokenResponse;
import com.example.repo_be_v2.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTokenRefreshService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public AccessTokenResponse execute(String requestedRefreshToken) {
        if (requestedRefreshToken == null || requestedRefreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        try {
            Claims claims = jwtTokenProvider.parseRefreshTokenClaims(requestedRefreshToken);
            Long userId = Long.valueOf(claims.getSubject());

            if (!refreshTokenService.matches(userId, requestedRefreshToken)) {
                throw new InvalidRefreshTokenException();
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(InvalidRefreshTokenException::new);
            String accessToken = jwtTokenProvider.generateAccessToken(user);

            return new AccessTokenResponse(accessToken);
        } catch (JwtException | NumberFormatException exception) {
            throw new InvalidRefreshTokenException();
        }
    }
}
