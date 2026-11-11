package com.example.repo_be_v2.global.security;

import com.example.repo_be_v2.global.error.ErrorResponse;
import com.example.repo_be_v2.global.error.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 시큐리티 필터 단계에서 끝나는 인증·권한 실패 응답.
 *
 * 필터에서 막힌 요청은 컨트롤러까지 가지 않아 GlobalExceptionHandler가 받지 못한다.
 * 그대로 두면 본문 없는 403이 나가므로, 여기서 다른 에러와 같은 형식으로 써 준다.
 *
 * 토큰이 왜 거절됐는지는 JwtAuthenticationFilter가 요청 속성에 남겨 둔다.
 * 만료(TOKEN_EXPIRED)와 그 밖의 실패를 구분해 줘야 화면에서 재발급을 시도할지 정할 수 있다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityErrorResponder implements AuthenticationEntryPoint, AccessDeniedHandler {

    public static final String ERROR_CODE_ATTRIBUTE = SecurityErrorResponder.class.getName() + ".errorCode";

    private final ObjectMapper objectMapper;

    // 인증 없이(또는 거절된 토큰으로) 인증이 필요한 경로를 부른 경우
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = request.getAttribute(ERROR_CODE_ATTRIBUTE) instanceof ErrorCode rejected
                ? rejected
                : ErrorCode.UNAUTHORIZED;

        write(request, response, errorCode);
    }

    // 인증은 됐지만 권한이 모자란 경우
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        write(request, response, ErrorCode.FORBIDDEN);
    }

    private void write(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) throws IOException {
        log.warn("인증·권한 실패: {} {} - {}", request.getMethod(), request.getRequestURI(), errorCode);

        response.setStatus(errorCode.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(errorCode));
    }
}
