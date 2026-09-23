package com.example.repo_be_v2.domain.notification.presentation;

import com.example.repo_be_v2.domain.notification.presentation.dto.response.NotificationReadResponse;
import com.example.repo_be_v2.domain.notification.presentation.dto.response.NotificationResponse;
import com.example.repo_be_v2.domain.notification.service.NotificationDeleteService;
import com.example.repo_be_v2.domain.notification.service.NotificationListService;
import com.example.repo_be_v2.domain.notification.service.NotificationReadService;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 알림 API.
 *
 * 명세의 경로 표기(alram)가 오타지만 프론트와 맞춘 값이라 그대로 두고,
 * 코드 쪽 이름만 notification으로 쓴다.
 */
@RestController
@RequestMapping("/alram")
@RequiredArgsConstructor
@Tag(name = "Alram", description = "홈 화면 알림 목록 조회·읽음 처리·삭제 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class NotificationController {

    private final NotificationListService notificationListService;
    private final NotificationReadService notificationReadService;
    private final NotificationDeleteService notificationDeleteService;

    // 알림 목록 조회 (인증된 사용자)
    @GetMapping
    @Operation(
            summary = "알림 목록 조회",
            description = "내 알림을 최신순으로 조회합니다. type과 resumeId·feedbackId로 눌렀을 때 이동할 대상을 알 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", useReturnTypeSchema = true)
    public List<NotificationResponse> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return notificationListService.execute(auth.getId());
    }

    // 알림 읽음 처리 (인증된 사용자)
    @PatchMapping("/{alramId}")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음으로 바꿉니다. 본인의 알림만 처리할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "읽음 처리 성공", useReturnTypeSchema = true)
    public NotificationReadResponse readNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "읽음 처리할 알림 ID", example = "66c73ec4c92f1d2d087e9012")
            @PathVariable String alramId
    ) {
        return notificationReadService.execute(auth.getId(), alramId);
    }

    // 알림 삭제 (인증된 사용자)
    @DeleteMapping("/{alramId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "알림 삭제", description = "알림을 지웁니다. 본인의 알림만 지울 수 있습니다.")
    @ApiResponse(responseCode = "204", description = "알림 삭제 성공")
    public void deleteNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "삭제할 알림 ID", example = "66c73ec4c92f1d2d087e9012")
            @PathVariable String alramId
    ) {
        notificationDeleteService.execute(auth.getId(), alramId);
    }
}
