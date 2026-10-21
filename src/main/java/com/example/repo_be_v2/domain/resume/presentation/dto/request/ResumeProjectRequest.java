package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 프로젝트 페이지 머리말.
 *
 * 작성 중에는 비어 있을 수 있어 필수 값이 없다.
 * 이름이 비었는지는 제출할 때 확인한다.
 *
 * 날짜는 문자열로 받는다. LocalDate로 받으면 형식이 틀렸을 때 JSON을 읽는 단계에서 실패해,
 * 어느 칸이 어떤 형식이어야 하는지 알려줄 수 없다.
 * 형식은 여기서, 실제로 있는 날짜인지는 변환할 때 확인한다.
 */
public record ResumeProjectRequest(

        @Size(max = 100, message = "프로젝트 이름은 100자 이하여야 합니다.")
        String name,

        //업로드·조회로 받은 서명 URL을 그대로 보내도 통과해야 한다. 임시 자격 증명을 쓰면 1KB를 넘기도 한다.
        @Size(max = 2048, message = "프로젝트 이미지 URL은 2048자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String imageUrl,

        @Size(max = 200, message = "프로젝트 소개는 200자 이하여야 합니다.")
        String summary,

        //비워 보내면 날짜가 없는 것으로 본다.
        @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "프로젝트 시작일은 yyyy-MM-dd 형식이어야 합니다.")
        String startDate,

        @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "프로젝트 종료일은 yyyy-MM-dd 형식이어야 합니다.")
        String endDate

) {
}
