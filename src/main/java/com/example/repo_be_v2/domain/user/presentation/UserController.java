package com.example.repo_be_v2.domain.user.presentation;

import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationConfirmRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationSendRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserLoginRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import com.example.repo_be_v2.domain.user.service.UserEmailSendService;
import com.example.repo_be_v2.domain.user.service.UserEmailVerifyService;
import com.example.repo_be_v2.domain.user.service.UserLoginService;
import com.example.repo_be_v2.domain.user.service.UserSignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 로그인, 이메일 인증 API")
public class UserController {
    private final UserLoginService userLoginService;
    private final UserSignUpService userSignUpService;
    private final UserEmailSendService userEmailSendService;
    private final UserEmailVerifyService userEmailVerifyService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "이메일 인증을 완료한 사용자를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true)
    public void signUp(@Valid @RequestBody UserSignUpRequest request) {
        userSignUpService.execute(request);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 확인하고 JWT 액세스 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true)
    public TokenResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userLoginService.execute(request);
    }

    @PostMapping("/email/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "이메일 인증 코드 발송", description = "DSM 이메일로 6자리 인증 코드를 전송합니다.")
    @ApiResponse(responseCode = "204", description = "인증 코드 발송 성공")
    public void sendEmailVerificationCode(@Valid @RequestBody EmailVerificationSendRequest request) {
        userEmailSendService.execute(request);
    }

    @PostMapping("/email/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "이메일 인증", description = "전송된 6자리 인증 코드를 검증합니다.")
    @ApiResponse(responseCode = "204", description = "이메일 인증 성공")
    public void verifyEmail(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        userEmailVerifyService.execute(request);
    }
}
