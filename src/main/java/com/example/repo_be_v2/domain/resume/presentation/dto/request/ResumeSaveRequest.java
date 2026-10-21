package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 이력서 저장 요청. 수동 저장과 자동 저장이 같은 형식을 쓴다.
 *
 * 첫 장 머리말 중 이름, 전공은 여기 없다.
 * 그 둘은 사용자 정보(MySQL)라 PATCH /user로 따로 고친다.
 *
 * 프로필 사진은 POST /image로 올려 받은 URL을 담는다.
 * 저장은 통째로 덮어쓰므로 사진을 바꾸지 않을 때도 조회한 값을 그대로 보내야 한다.
 */
public record ResumeSaveRequest(

        @Size(max = 1000, message = "자기소개는 1000자 이하여야 합니다.")
        String introduce,

        //@Email만으로는 "a@b"처럼 도메인에 점이 없는 주소도 통과한다. 연락용이라 실제로 보낼 수 있는 모양만 받는다.
        //프런트는 비워 둘 때 빈 문자열을 보내므로 빈 값은 통과시킨다.
        @Email(message = "유효하지 않은 이메일 형식입니다.")
        @Pattern(
                regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$",
                message = "유효하지 않은 이메일 형식입니다."
        )
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        @Size(max = 50, message = "기술스택은 50개까지 등록할 수 있습니다.")
        List<
                @NotNull(message = "비어 있는 기술스택이 있습니다.")
                @Size(max = 30, message = "기술스택은 30자 이하여야 합니다.")
                String> skills,

        //프런트는 URL을 지울 때 빈 문자열을 보낸다. 빈 값은 통과시키고, 값이 있으면 URL이어야 한다.
        @Size(max = 2048, message = "포트폴리오 URL은 2048자 이하여야 합니다.")
        @Pattern(regexp = "^$|^https?://.+", message = "포트폴리오 URL은 http:// 또는 https://로 시작해야 합니다.")
        String portfolioUrl,

        //업로드·조회로 받은 서명 URL을 그대로 보내도 통과해야 한다. 임시 자격 증명을 쓰면 1KB를 넘기도 한다.
        @Size(max = 2048, message = "프로필 이미지 URL은 2048자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String profileImageUrl,

        //리스트 안의 null은 @Valid가 건너뛰어 변환 중에 NPE가 난다. 원소에도 따로 건다.
        //화면이 최대 5쪽까지만 만들 수 있다.
        @NotNull(message = "이력서 페이지 목록이 필요합니다.")
        @Size(max = 5, message = "이력서는 최대 5쪽까지 작성할 수 있습니다.")
        @Valid
        List<@NotNull(message = "비어 있는 페이지가 있습니다.") ResumePageRequest> pages

) {
}
