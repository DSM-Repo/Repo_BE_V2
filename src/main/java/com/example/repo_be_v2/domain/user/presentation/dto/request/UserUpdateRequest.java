package com.example.repo_be_v2.domain.user.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 내 정보 수정 요청. 변경할 필드만 보내는 부분 수정이라 모든 필드가 선택이다.
 * 보내지 않은(null) 필드는 건드리지 않는다.
 */
public record UserUpdateRequest(

        //선생님이 만들어둔 전공(tbl_major)의 id
        Long majorId,

        @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String profileImageUrl
) {
}
