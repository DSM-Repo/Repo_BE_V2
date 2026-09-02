package com.example.repo_be_v2.domain.resume.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumePageRequest;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//이력서 서비스들이 공통으로 쓰는 조회와 변환을 모아둔다.
@Component
@RequiredArgsConstructor
public class ResumeReader {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //MySQL에 실제 사용자가 존재하는지 확인
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    //유저 소유의 이력서를 id 기준으로 조회
    public Resume getResumeByIdAndUserId(String resumeId, Long userId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    //유저 소유의 이력서를 조회
    public Resume getResumeByUserId(Long userId) {
        return resumeRepository.findByUserId(userId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    /**
     * 요청 DTO의 페이지 목록을 도메인 객체로 변환한다.
     *
     * 페이지 id는 피드백이 물고 있는 값이라 저장할 때마다 새로 만들면 안 된다.
     * 요청이 id를 들고 오면 그대로 쓰고,
     * 비워서 보내면 같은 자리에 있던 기존 페이지의 id를 물려준다.
     * 둘 다 없을 때(새 페이지, id 없이 저장된 옛 데이터)만 새로 발급한다.
     */
    public List<ResumePage> toResumePages(Resume resume, List<ResumePageRequest> pageRequests) {
        List<ResumePage> previousPages = resume == null ? List.of() : resume.getPages();

        return pageRequests.stream()
                .map(page -> new ResumePage(
                        resolvePageId(previousPages, page),
                        page.index(),
                        page.content()
                ))
                .toList();
    }

    private String resolvePageId(List<ResumePage> previousPages, ResumePageRequest request) {
        if (request.id() != null && !request.id().isBlank()) {
            return request.id();
        }

        return findPreviousPageId(previousPages, request.index())
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private Optional<String> findPreviousPageId(List<ResumePage> previousPages, int index) {
        if (previousPages == null) {
            return Optional.empty();
        }

        return previousPages.stream()
                .filter(page -> page.getIndex() == index && page.getId() != null)
                .map(ResumePage::getId)
                .findFirst();
    }
}
