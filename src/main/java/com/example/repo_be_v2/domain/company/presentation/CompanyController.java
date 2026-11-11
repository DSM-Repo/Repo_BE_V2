package com.example.repo_be_v2.domain.company.presentation;

import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyCreateRequest;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyListResponse;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyResponse;
import com.example.repo_be_v2.domain.company.service.CompanyCreateService;
import com.example.repo_be_v2.domain.company.service.CompanyDeleteService;
import com.example.repo_be_v2.domain.company.service.CompanyListService;
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
@Tag(name = "Company", description = "도서관만 열람할 수 있는 기업 계정을 선생님이 발급·관리하는 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class CompanyController {

    private final CompanyCreateService companyCreateService;
    private final CompanyListService companyListService;
    private final CompanyDeleteService companyDeleteService;

    // 기업 계정 발급 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "기업 계정 발급",
            description = "선생님이 회사 하나에 계정을 발급합니다. 기업은 이 이메일과 비밀번호로 POST /user/login에 로그인하고, 도서관 조회만 할 수 있습니다. 학교 이메일(dsm.hs.kr)은 쓸 수 없습니다."
    )
    @ApiResponse(responseCode = "201", description = "기업 계정 발급 성공", useReturnTypeSchema = true)
    public CompanyResponse createCompany(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody CompanyCreateRequest request
    ) {
        return companyCreateService.execute(auth.getId(), request);
    }

    // 기업 계정 목록 조회 (선생님 권한)
    @GetMapping
    @Operation(summary = "기업 계정 목록 조회", description = "선생님이 발급한 기업 계정을 회사 이름 오름차순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "기업 계정 목록 조회 성공", useReturnTypeSchema = true)
    public CompanyListResponse getCompanies(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return companyListService.execute(auth.getId());
    }

    // 기업 계정 삭제 (선생님 권한)
    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "기업 계정 삭제", description = "선생님이 기업 계정을 삭제합니다. 삭제하면 그 계정의 토큰은 바로 쓸 수 없게 됩니다.")
    @ApiResponse(responseCode = "204", description = "기업 계정 삭제 성공")
    public void deleteCompany(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "삭제할 기업 계정 ID", example = "1")
            @PathVariable Long companyId
    ) {
        companyDeleteService.execute(auth.getId(), companyId);
    }
}
