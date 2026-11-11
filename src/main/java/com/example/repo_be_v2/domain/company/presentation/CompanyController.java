package com.example.repo_be_v2.domain.company.presentation;

import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyCreateRequest;
import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyLoginRequest;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyListResponse;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyResponse;
import com.example.repo_be_v2.domain.company.service.CompanyCreateService;
import com.example.repo_be_v2.domain.company.service.CompanyDeleteService;
import com.example.repo_be_v2.domain.company.service.CompanyListService;
import com.example.repo_be_v2.domain.company.service.CompanyLoginService;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Tag(name = "Company", description = "도서관만 열람할 수 있는 기업 계정을 선생님이 발급·관리하고, 기업이 인증 코드로 로그인하는 API")
public class CompanyController {

    private final CompanyCreateService companyCreateService;
    private final CompanyListService companyListService;
    private final CompanyDeleteService companyDeleteService;
    private final CompanyLoginService companyLoginService;

    // 기업 계정 발급 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "기업 계정 발급",
            description = "선생님이 회사 이름을 넣어 계정을 만들면 인증 코드(code)가 발급됩니다. 기업은 이 코드로 POST /company/login에 로그인하고, 도서관 조회만 할 수 있습니다. 같은 이름의 회사는 한 번만 만들 수 있습니다."
    )
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    @ApiResponse(responseCode = "201", description = "기업 계정 발급 성공", useReturnTypeSchema = true)
    public CompanyResponse createCompany(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody CompanyCreateRequest request
    ) {
        return companyCreateService.execute(auth.getId(), request);
    }

    // 기업 계정 목록 조회 (선생님 권한)
    @GetMapping
    @Operation(summary = "기업 계정 목록 조회", description = "선생님이 발급한 기업 계정을 회사 이름 오름차순으로 조회합니다. 인증 코드도 함께 내려주므로 언제든 다시 확인할 수 있습니다.")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    @ApiResponse(responseCode = "200", description = "기업 계정 목록 조회 성공", useReturnTypeSchema = true)
    public CompanyListResponse getCompanies(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return companyListService.execute(auth.getId());
    }

    // 기업 계정 삭제 (선생님 권한)
    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "기업 계정 삭제", description = "선생님이 기업 계정을 삭제합니다. 삭제하면 인증 코드와 그 코드로 받은 토큰을 바로 쓸 수 없게 됩니다.")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    @ApiResponse(responseCode = "204", description = "기업 계정 삭제 성공")
    public void deleteCompany(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "삭제할 기업 계정 ID", example = "1")
            @PathVariable Long companyId
    ) {
        companyDeleteService.execute(auth.getId(), companyId);
    }

    // 기업 로그인 (인증 없이 호출)
    @PostMapping("/login")
    @Operation(
            summary = "기업 로그인",
            description = "선생님에게 받은 인증 코드를 확인하고 JWT 액세스 토큰과 리프레시 토큰을 발급합니다. 코드는 구분선(-)이나 공백을 넣든 빼든, 소문자로 보내든 받습니다. 토큰 재발급은 POST /user/refresh를 그대로 씁니다."
    )
    @ApiResponse(responseCode = "200", description = "기업 로그인 성공", useReturnTypeSchema = true)
    public TokenResponse login(@Valid @RequestBody CompanyLoginRequest request) {
        return companyLoginService.execute(request);
    }
}
