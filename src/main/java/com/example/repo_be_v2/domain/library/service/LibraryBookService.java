package com.example.repo_be_v2.domain.library.service;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibraryResumeResponse;
import com.example.repo_be_v2.domain.library.service.support.LibraryReader;
import com.example.repo_be_v2.domain.library.service.support.LibraryResumeMapper;
import com.example.repo_be_v2.domain.resume.domain.Resume;
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
public class LibraryBookService {

    private final LibraryReader libraryReader;
    private final LibraryResumeMapper libraryResumeMapper;

    /**
     * 레주메북 전체 조회
     *
     * 한 학년도에 공개된 이력서를 본문까지 한 번에 돌려준다.
     * PDF 변환 서버가 학생마다 단건 조회를 반복하지 않게 하려는 것이 목적이라,
     * 학생 수와 상관없이 MongoDB 한 번, MySQL 한 번만 조회한다.
     */
    @Transactional(readOnly = true)
    public List<LibraryResumeResponse> execute(int date, Integer grade, String major) {
        List<Resume> resumes = libraryReader.getPublicResumesWithContent(date);

        if (resumes.isEmpty()) {
            return List.of();
        }

        Map<Long, User> students = libraryReader.getUsersById(
                resumes.stream().map(Resume::getUserId).toList()
        );

        return resumes.stream()
                .map(resume -> toResponse(resume, students.get(resume.getUserId())))
                .filter(Objects::nonNull)
                .filter(response -> matchesGrade(response, grade))
                .filter(response -> matchesMajor(response, major))
                .sorted(Comparator.comparing(LibraryResumeResponse::studentNumber)
                        .thenComparing(LibraryResumeResponse::name))
                .toList();
    }

    /**
     * 이력서 한 건을 응답으로 바꾼다. 바꿀 수 없으면 null.
     *
     * 탈퇴 등으로 사용자가 사라졌으면 이름도 학년도 알 수 없다.
     * 목록 조회와 같은 규칙으로 조용히 뺀다.
     */
    private LibraryResumeResponse toResponse(Resume resume, User student) {
        if (student == null) {
            return null;
        }

        return libraryResumeMapper.toResponse(resume, student);
    }

    //학년은 저장된 값이 아니라 계산 결과라 조회 뒤에 거른다.
    private boolean matchesGrade(LibraryResumeResponse response, Integer grade) {
        return grade == null || response.year() == grade;
    }

    private boolean matchesMajor(LibraryResumeResponse response, String major) {
        return major == null || major.isBlank() || major.equals(response.majorName());
    }
}
