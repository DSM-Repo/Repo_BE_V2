package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.InvalidCredentialsException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserLoginRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import com.example.repo_be_v2.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public TokenResponse execute(UserLoginRequest request) {
        User user = userRepository.findByStudentEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getStudentPassword())) {
            throw new InvalidCredentialsException();
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.save(
                user.getId(),
                refreshToken
        );

        return new TokenResponse(
                "Bearer",
                accessToken,
                refreshToken
        );
    }
}
