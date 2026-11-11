package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.CompanyCode;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.exception.CompanyCodeInvalidException;
import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyLoginRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import com.example.repo_be_v2.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyLoginService {

    private final CompanyRepository companyRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 기업 로그인
     *
     * 선생님에게 받은 인증 코드만 입력하면 된다. 코드가 맞으면 학생·선생님 로그인과 같은 모양의 토큰을 준다.
     * 모양이 틀린 코드와 없는 코드는 같은 에러로 답해, 어떤 코드가 있는지 알려주지 않는다.
     *
     * 리프레시 토큰을 Redis에 저장하지 않는다.
     * 한 회사의 여러 담당자가 같은 코드로 동시에 로그인해 쓰는데, 학생·선생님처럼 계정당 하나만 저장하면
     * 누가 새로 로그인할 때마다 먼저 로그인한 사람의 재발급이 막힌다.
     * 차단은 계정 삭제로만 하고, 재발급 때 계정이 남아 있는지 확인하는 것으로 충분하다.
     */
    @Transactional(readOnly = true)
    public TokenResponse execute(CompanyLoginRequest request) {
        Company company = CompanyCode.normalize(request.code())
                .flatMap(companyRepository::findByCode)
                .orElseThrow(CompanyCodeInvalidException::new);

        return new TokenResponse(
                "Bearer",
                jwtTokenProvider.generateAccessToken(company),
                jwtTokenProvider.generateRefreshToken(company)
        );
    }
}
