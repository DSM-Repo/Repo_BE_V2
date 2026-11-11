package com.example.repo_be_v2.global.security.jwt;

import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.security.SecurityErrorResponder;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null
                && authorization.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticate(request, authorization.substring(BEARER_PREFIX.length()));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰이 거절되면 여기서 응답을 끊지 않고 이유만 요청에 남긴 채 넘긴다.
     * 로그인처럼 인증이 필요 없는 경로는 낡은 토큰이 붙어 와도 그대로 통과해야 하기 때문이다.
     * 인증이 필요한 경로였다면 SecurityErrorResponder가 남겨 둔 이유로 401을 만든다.
     */
    private void authenticate(HttpServletRequest request, String token) {
        try {
            AuthDetail principal = findAccount(jwtTokenProvider.parseAccessTokenClaims(token));
            if (principal == null) {
                //토큰은 멀쩡하지만 그 사이 탈퇴했거나 삭제된 계정이다.
                request.setAttribute(SecurityErrorResponder.ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_TOKEN);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException expired) {
            SecurityContextHolder.clearContext();
            request.setAttribute(SecurityErrorResponder.ERROR_CODE_ATTRIBUTE, ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException invalid) {
            SecurityContextHolder.clearContext();
            request.setAttribute(SecurityErrorResponder.ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰의 주인을 찾는다. 없으면 null이다.
     *
     * 기업은 tbl_company에, 학생·선생님은 tbl_user에 있어 토큰의 role로 찾을 곳을 고른다.
     * 요청마다 다시 찾으므로 선생님이 기업 계정을 삭제하면 남아 있는 토큰도 바로 막힌다.
     */
    private AuthDetail findAccount(Claims claims) {
        String email = claims.getSubject();

        if (jwtTokenProvider.isCompany(claims)) {
            return companyRepository.findByEmail(email)
                    .map(AuthDetail::from)
                    .orElse(null);
        }

        return userRepository.findByStudentEmail(email)
                .map(AuthDetail::from)
                .orElse(null);
    }
}
