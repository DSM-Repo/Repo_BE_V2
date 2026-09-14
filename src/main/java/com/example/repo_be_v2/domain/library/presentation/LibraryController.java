package com.example.repo_be_v2.domain.library.presentation;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryGroupResponse;
import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumeResponse;
import com.example.repo_be_v2.domain.library.presentation.dto.response.LibrarySearchResponse;
import com.example.repo_be_v2.domain.library.service.LibraryGetService;
import com.example.repo_be_v2.domain.library.service.LibraryListService;
import com.example.repo_be_v2.domain.library.service.LibrarySearchService;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
@Tag(name = "Library", description = "공개된 선배 이력서를 학년도별로 모아 보는 도서관 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class LibraryController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final LibraryListService libraryListService;
    private final LibrarySearchService librarySearchService;
    private final LibraryGetService libraryGetService;

    // 공개 도서관 조회 (인증된 사용자)
    @GetMapping
    @Operation(
            summary = "공개 도서관 조회",
            description = "공개된 이력서를 학년도·학년·기수 묶음으로 조회합니다. 묶음 안의 학생은 검색 API를 date로 필터해 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "도서관 조회 성공", useReturnTypeSchema = true)
    public List<LibraryGroupResponse> getLibrary() {
        return libraryListService.execute();
    }

    // 학생 검색 (인증된 사용자)
    @GetMapping("/search")
    @Operation(summary = "학생 검색", description = "공개된 이력서를 학생 이름으로 검색합니다. 전공과 학년도로 좁힐 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "학생 검색 성공", useReturnTypeSchema = true)
    public LibrarySearchResponse searchStudents(
            @Parameter(description = "학생 이름 검색어", example = "김태균")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "전공 이름", example = "백엔드")
            @RequestParam(required = false) String major,
            @Parameter(description = "공개된 학년도", example = "2026")
            @RequestParam(required = false) Integer date,
            @Parameter(description = "페이지 번호 (0부터)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size
    ) {
        return librarySearchService.execute(keyword, major, date, page, size);
    }

    // 학생 이력서 단일 조회 (인증된 사용자)
    @GetMapping("/{studentId}")
    @Operation(summary = "학생 이력서 단일 조회", description = "공개된 이력서를 작성된 그대로 조회합니다. 비공개 이력서는 조회되지 않습니다.")
    @ApiResponse(responseCode = "200", description = "이력서 조회 성공", useReturnTypeSchema = true)
    public LibraryResumeResponse getStudentResume(
            @Parameter(description = "조회할 학생 ID", example = "1")
            @PathVariable Long studentId
    ) {
        return libraryGetService.execute(studentId);
    }
}
