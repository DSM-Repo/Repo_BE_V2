package com.example.repo_be_v2.domain.user.presentation;

import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationConfirmRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationSendRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserLoginRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserUpdateRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.AccessTokenResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserMypageResponse;
import com.example.repo_be_v2.domain.user.service.UserEmailSendService;
import com.example.repo_be_v2.domain.user.service.UserEmailVerifyService;
import com.example.repo_be_v2.domain.user.service.UserLoginService;
import com.example.repo_be_v2.domain.user.service.UserMypageService;
import com.example.repo_be_v2.domain.user.service.UserSignUpService;
import com.example.repo_be_v2.domain.user.service.UserTokenRefreshService;
import com.example.repo_be_v2.domain.user.service.UserUpdateService;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 로그인, 이메일 인증, 내 정보 조회·수정 API")
public class UserController {
    private final UserLoginService userLoginService;
    private final UserSignUpService userSignUpService;
    private final UserEmailSendService userEmailSendService;
    private final UserEmailVerifyService userEmailVerifyService;
    private final UserTokenRefreshService userTokenRefreshService;
    private final UserMypageService userMypageService;
    private final UserUpdateService userUpdateService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "이메일 인증을 완료한 사용자를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true)
    public void signUp(@Valid @RequestBody UserSignUpRequest request) {
        userSignUpService.execute(request);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 확인하고 JWT 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true)
    public TokenResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userLoginService.execute(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh-Token 헤더의 리프레시 토큰을 확인하고 새로운 액세스 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", useReturnTypeSchema = true)
    public AccessTokenResponse refresh(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken
    ) {
        return userTokenRefreshService.execute(refreshToken);
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

    // 내 정보 조회 (인증된 사용자) — 홈 화면용, 이력서 작성 진행률 포함
    @GetMapping
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    @Operation(
            summary = "내 정보 조회",
            description = "로그인한 사용자의 정보, 한줄소개, 이력서 작성 진행률을 한 번에 조회합니다. 이력서가 없으면 진행률은 0%입니다."
    )
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공", useReturnTypeSchema = true)
    public UserMypageResponse getMypage(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return userMypageService.execute(auth.getId());
    }

    // 내 정보 수정 (인증된 사용자) — 전공 선택, 프로필 이미지
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    @Operation(
            summary = "내 정보 수정",
            description = "전공과 프로필 이미지를 수정합니다. 보낸 필드만 바뀝니다. 프로필 이미지는 POST /image로 먼저 올린 URL을 보냅니다."
    )
    @ApiResponse(responseCode = "204", description = "내 정보 수정 성공")
    public void updateMypage(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userUpdateService.execute(auth.getId(), request);
    }
}
