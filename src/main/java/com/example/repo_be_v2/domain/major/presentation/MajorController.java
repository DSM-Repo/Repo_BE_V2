package com.example.repo_be_v2.domain.major.presentation;

import com.example.repo_be_v2.domain.major.presentation.dto.request.MajorCreateRequest;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorListResponse;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorResponse;
import com.example.repo_be_v2.domain.major.service.MajorCreateService;
import com.example.repo_be_v2.domain.major.service.MajorDeleteService;
import com.example.repo_be_v2.domain.major.service.MajorListService;
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
@RequestMapping("/major")
@RequiredArgsConstructor
@Tag(name = "Major", description = "전공 목록 조회 및 관리 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class MajorController {

    private final MajorListService majorListService;
    private final MajorCreateService majorCreateService;
    private final MajorDeleteService majorDeleteService;

    // 전공 목록 조회 (인증된 사용자)
    @GetMapping
    @Operation(summary = "전공 목록 조회", description = "등록된 전공을 이름 오름차순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전공 목록 조회 성공", useReturnTypeSchema = true)
    public MajorListResponse getMajors() {
        return majorListService.execute();
    }

    // 전공 추가 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "전공 추가", description = "선생님이 학생들이 고를 수 있는 전공을 추가합니다.")
    @ApiResponse(responseCode = "201", description = "전공 추가 성공", useReturnTypeSchema = true)
    public MajorResponse createMajor(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody MajorCreateRequest request
    ) {
        return majorCreateService.execute(auth.getId(), request);
    }

    // 전공 삭제 (선생님 권한)
    @DeleteMapping("/{majorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "전공 삭제", description = "선생님이 전공을 삭제합니다. 학생이 쓰고 있는 전공은 삭제할 수 없습니다.")
    @ApiResponse(responseCode = "204", description = "전공 삭제 성공")
    public void deleteMajor(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "삭제할 전공 ID", example = "1")
            @PathVariable Long majorId
    ) {
        majorDeleteService.execute(auth.getId(), majorId);
    }
}
