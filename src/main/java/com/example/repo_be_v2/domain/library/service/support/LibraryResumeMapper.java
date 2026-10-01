package com.example.repo_be_v2.domain.library.service.support;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumePageResponse;
import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumeResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 공개된 이력서와 그 주인을 도서관 응답으로 바꾼다.
 *
 * 단일 조회와 레주메북 전체 조회가 같은 값을 내려줘야 해서 변환을 여기 모아둔다.
 * 학년도·학년·기수처럼 저장하지 않고 계산하는 값들이 두 경로에서 어긋나면 안 되기 때문이다.
 */
@Component
@RequiredArgsConstructor
public class LibraryResumeMapper {

    private final LibraryReader libraryReader;

    public LibraryResumeResponse toResponse(Resume resume, User student) {
        int cohort = libraryReader.cohortOf(student);
        int schoolYear = schoolYearOf(resume);
        int grade = libraryReader.gradeOf(schoolYear, cohort);

        return new LibraryResumeResponse(
                resume.getId(),
                student.getId(),
                student.getStudentName(),
                libraryReader.studentNumberOf(grade, student),
                emailOf(resume, student),
                student.getMajorName(),
                resume.profileImageUrlOr(student.getProfileImageUrl()),
                resume.getIntroduce(),
                resume.getSkills(),
                resume.getPortfolioUrl(),
                schoolYear,
                grade,
                cohort,
                resume.getReleasedAt(),
                toPageResponses(resume.getPages())
        );
    }

    /**
     * 공개 시점이 비어 있으면 저장 시점으로 대신한다.
     *
     * 도메인상 공개된 이력서에는 releasedAt이 항상 있지만,
     * 없더라도 학년도를 못 정해 실패하는 것보다 저장 시점으로 보여주는 편이 낫다.
     */
    private int schoolYearOf(Resume resume) {
        return resume.getReleasedAt() == null
                ? libraryReader.schoolYearOf(resume.getSavedAt())
                : libraryReader.schoolYearOf(resume.getReleasedAt());
    }

    /**
     * 도서관에 보여줄 이메일.
     *
     * 첫 장에 적은 연락용 이메일을 우선 쓰고,
     * 아직 적지 않았으면 학교 계정 이메일로 대신한다.
     */
    private String emailOf(Resume resume, User student) {
        String email = resume.getEmail();

        return email == null || email.isBlank()
                ? student.getStudentEmail()
                : email;
    }

    private List<LibraryResumePageResponse> toPageResponses(List<ResumePage> pages) {
        if (pages == null) {
            return List.of();
        }

        return pages.stream()
                .map(LibraryResumePageResponse::from)
                .toList();
    }
}
