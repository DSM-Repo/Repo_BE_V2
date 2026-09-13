package com.example.repo_be_v2.domain.library.service;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumePageResponse;
import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumeResponse;
import com.example.repo_be_v2.domain.library.service.support.LibraryReader;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryGetService {

    private final LibraryReader libraryReader;

    /**
     * 학생 이력서 단일 조회
     *
     * 이력서 본문은 작성된 HTML을 그대로 내려준다.
     * 도서관은 선배들의 이력서를 그대로 보는 곳이라 따로 가공하지 않는다.
     */
    @Transactional(readOnly = true)
    public LibraryResumeResponse execute(Long studentId) {
        Resume resume = libraryReader.getPublicResume(studentId);
        User student = libraryReader.getUser(studentId);

        int cohort = libraryReader.cohortOf(student);
        int schoolYear = resume.getReleasedAt() == null
                ? libraryReader.schoolYearOf(resume.getSavedAt())
                : libraryReader.schoolYearOf(resume.getReleasedAt());
        int grade = libraryReader.gradeOf(schoolYear, cohort);

        return new LibraryResumeResponse(
                resume.getId(),
                student.getId(),
                student.getStudentName(),
                libraryReader.studentNumberOf(grade, student),
                student.getStudentEmail(),
                student.getMajorName(),
                null,
                resume.getIntroduce(),
                resume.getPortfolioUrl(),
                schoolYear,
                grade,
                cohort,
                resume.getReleasedAt(),
                toPageResponses(resume.getPages())
        );
    }

    private List<LibraryResumePageResponse> toPageResponses(List<ResumePage> pages) {
        if (pages == null) {
            return List.of();
        }

        return pages.stream()
                .map(page -> new LibraryResumePageResponse(
                        page.getId(),
                        page.getIndex(),
                        page.getContent()
                ))
                .toList();
    }
}
