package com.example.repo_be_v2.domain.library.service;

import com.example.repo_be_v2.domain.library.presentation.dto.response.LibrarySearchItemResponse;
import com.example.repo_be_v2.domain.library.presentation.dto.response.LibrarySearchResponse;
import com.example.repo_be_v2.domain.library.service.support.LibraryReader;
import com.example.repo_be_v2.domain.resume.domain.repository.PublicResumeSummary;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibrarySearchService {

    private final LibraryReader libraryReader;
    private final UserRepository userRepository;

    /**
     * 학생 검색
     *
     * 공개 여부·공개 시점은 MongoDB에, 이름·전공은 MySQL에 있어 한 쿼리로 묶을 수 없다.
     * 그래서 MongoDB에서 공개된 이력서의 userId를 먼저 추린 뒤 MySQL에 넘겨
     * 이름·전공 필터와 페이징을 한쪽에서 처리한다. 그래야 totalElements가 정확하다.
     */
    @Transactional(readOnly = true)
    public LibrarySearchResponse execute(
            String keyword,
            String major,
            Integer date,
            int page,
            int size
    ) {
        List<Long> studentIds = libraryReader.getPublicResumes(date)
                .stream()
                .map(PublicResumeSummary::getUserId)
                .toList();

        //공개된 이력서가 없으면 MySQL을 볼 것도 없다. 빈 IN 절은 쿼리도 깨뜨린다.
        if (studentIds.isEmpty()) {
            return new LibrarySearchResponse(List.of(), 0);
        }

        Page<User> students = userRepository.searchInIds(
                studentIds,
                blankToNull(keyword),
                blankToNull(major),
                PageRequest.of(page, size)
        );

        return new LibrarySearchResponse(
                students.getContent()
                        .stream()
                        .map(student -> new LibrarySearchItemResponse(
                                student.getId(),
                                student.getStudentName(),
                                student.getMajorName()
                        ))
                        .toList(),
                students.getTotalElements()
        );
    }

    //필터를 걸지 않은 것과 빈 문자열로 거르는 것을 같게 취급한다.
    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
