package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRefreshTokenService refreshTokenService;

    /**
     * 로그인
     *
     * 학생·선생님과 기업이 같은 API로 로그인한다. 화면이 계정 종류를 먼저 묻지 않아도 되게 하려는 것이다.
     * 기업 계정은 학교 이메일로 만들 수 없어 두 테이블의 이메일이 겹치지 않으므로,
     * tbl_user에 없으면 기업 계정에서 찾는다.
     */
    @Transactional(readOnly = true)
    public TokenResponse execute(UserLoginRequest request) {
        User user = userRepository.findByStudentEmail(request.email()).orElse(null);

        if (user == null) {
            return loginCompany(request);
        }

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

    private TokenResponse loginCompany(UserLoginRequest request) {
        Company company = companyRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), company.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtTokenProvider.generateAccessToken(company);
        String refreshToken = jwtTokenProvider.generateRefreshToken(company);

        refreshTokenService.saveForCompany(company.getId(), refreshToken);

        return new TokenResponse(
                "Bearer",
                accessToken,
                refreshToken
        );
    }
}
