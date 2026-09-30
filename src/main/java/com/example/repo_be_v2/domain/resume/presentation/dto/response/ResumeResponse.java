package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 내 이력서 조회 응답.
 *
 * 저장 요청에 담은 값을 그대로 돌려주고,
 * 서버가 가진 값(이름, 전공, 프로필 사진, 상태)만 더한다.
 * 프런트는 받은 그대로 고쳐서 다시 저장 요청으로 보내면 된다.
 */
public record ResumeResponse(
        String id,
        String name,
        String introduce,
        String email,
        List<String> skills,
        String portfolioUrl,
        boolean isPublic,
        String profileImageUrl,
        String majorName,
        ResumeSubmissionStatus submissionStatus,
        LocalDateTime savedAt,
        List<ResumePageResponse> pages
) {

    /**
     * 이력서와 그 주인의 사용자 정보를 합쳐 응답으로 만든다.
     *
     * 본인 조회와 선생님 조회가 같은 형식을 내려줘야 해서 변환을 여기 모아둔다.
     * owner는 이력서를 쓴 학생이다. 선생님이 조회할 때도 학생의 이름·전공이 들어간다.
     */
    public static ResumeResponse of(Resume resume, User owner) {
        return new ResumeResponse(
                resume.getId(),
                owner.getStudentName(),
                resume.getIntroduce(),
                resume.getEmail(),
                resume.getSkills(),
                resume.getPortfolioUrl(),
                resume.isPublic(),
                owner.getProfileImageUrl(),
                owner.getMajorName(),
                resume.getSubmissionStatus(),
                resume.getSavedAt(),
                toPageResponses(resume.getPages())
        );
    }

    private static List<ResumePageResponse> toPageResponses(List<ResumePage> pages) {
        if (pages == null) {
            return List.of();
        }

        return pages.stream()
                .map(ResumePageResponse::from)
                .toList();
    }
}
