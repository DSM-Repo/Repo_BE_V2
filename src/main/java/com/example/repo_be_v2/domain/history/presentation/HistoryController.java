package com.example.repo_be_v2.domain.history.presentation;

import com.example.repo_be_v2.domain.history.presentation.dto.request.HistoryRequest;
import com.example.repo_be_v2.domain.history.presentation.dto.response.HistoryResponse;
import com.example.repo_be_v2.domain.history.service.HistoryCreateService;
import com.example.repo_be_v2.domain.history.service.HistoryDeleteService;
import com.example.repo_be_v2.domain.history.service.HistoryListService;
import com.example.repo_be_v2.domain.history.service.HistoryUpdateService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "히스토리 조회·등록·수정·삭제 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class HistoryController {

    private final HistoryListService historyListService;
    private final HistoryCreateService historyCreateService;
    private final HistoryUpdateService historyUpdateService;
    private final HistoryDeleteService historyDeleteService;

    // 히스토리 목록 조회 (인증된 사용자)
    @GetMapping
    @Operation(summary = "히스토리 목록 조회", description = "히스토리를 최신 날짜순으로 조회합니다. 같은 날짜는 나중에 등록한 것이 먼저 옵니다.")
    @ApiResponse(responseCode = "200", description = "히스토리 목록 조회 성공", useReturnTypeSchema = true)
    public List<HistoryResponse> getHistories() {
        return historyListService.execute();
    }

    // 히스토리 등록 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "히스토리 등록", description = "선생님이 히스토리를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "히스토리 등록 성공", useReturnTypeSchema = true)
    public HistoryResponse createHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody HistoryRequest request
    ) {
        return historyCreateService.execute(auth.getId(), request);
    }

    // 히스토리 수정 (선생님 권한)
    @PatchMapping("/{historyId}")
    @Operation(summary = "히스토리 수정", description = "선생님이 히스토리의 날짜와 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "히스토리 수정 성공", useReturnTypeSchema = true)
    public HistoryResponse updateHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "수정할 히스토리 ID", example = "66c73ec4c92f1d2d087e9012")
            @PathVariable String historyId,
            @Valid @RequestBody HistoryRequest request
    ) {
        return historyUpdateService.execute(auth.getId(), historyId, request);
    }

    // 히스토리 삭제 (선생님 권한)
    @DeleteMapping("/{historyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "히스토리 삭제", description = "선생님이 히스토리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "히스토리 삭제 성공")
    public void deleteHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "삭제할 히스토리 ID", example = "66c73ec4c92f1d2d087e9012")
            @PathVariable String historyId
    ) {
        historyDeleteService.execute(auth.getId(), historyId);
    }
}
