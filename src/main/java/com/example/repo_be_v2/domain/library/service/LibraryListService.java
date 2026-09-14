package com.example.repo_be_v2.domain.library.service;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryGroupResponse;
import com.example.repo_be_v2.domain.library.service.support.LibraryReader;
import com.example.repo_be_v2.domain.resume.domain.repository.PublicResumeSummary;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LibraryListService {

    private final LibraryReader libraryReader;

    /**
     * 공개 도서관 조회
     *
     * 공개된 이력서를 학년도·학년·기수로 묶어 목록만 돌려준다.
     * 묶음 안의 학생 목록은 검색 API가 학년도 필터로 처리한다.
     */
    @Transactional(readOnly = true)
    public List<LibraryGroupResponse> execute() {
        List<PublicResumeSummary> resumes = libraryReader.getPublicResumes(null);

        if (resumes.isEmpty()) {
            return List.of();
        }

        Map<Long, User> users = libraryReader.getUsersById(
                resumes.stream().map(PublicResumeSummary::getUserId).toList()
        );

        return resumes.stream()
                .map(resume -> toGroup(resume, users.get(resume.getUserId())))
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparingInt(LibraryGroupResponse::date).reversed()
                        .thenComparingInt(LibraryGroupResponse::year))
                .toList();
    }

    /**
     * 이력서 한 건을 묶음으로 바꾼다. 묶을 수 없으면 null.
     *
     * 공개 상태인데 releasedAt이 비어 있으면 학년도를 정할 수 없고,
     * 탈퇴 등으로 사용자가 사라졌으면 학년을 알 수 없다.
     * 둘 다 목록에서 조용히 빼는 편이 낫다.
     */
    private LibraryGroupResponse toGroup(PublicResumeSummary resume, User user) {
        if (resume.getReleasedAt() == null || user == null) {
            return null;
        }

        int schoolYear = libraryReader.schoolYearOf(resume.getReleasedAt());
        int cohort = libraryReader.cohortOf(user);

        return new LibraryGroupResponse(
                schoolYear,
                libraryReader.gradeOf(schoolYear, cohort),
                cohort
        );
    }
}
