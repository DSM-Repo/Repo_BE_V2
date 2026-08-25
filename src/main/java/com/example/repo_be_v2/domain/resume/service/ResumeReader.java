package com.example.repo_be_v2.domain.resume.service;

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

    //요청 DTO의 페이지 목록을 도메인 객체로 변환
    public List<ResumePage> toResumePages(List<ResumePageRequest> pageRequests) {
        return pageRequests.stream()
                .map(page -> new ResumePage(
                        page.index(),
                        page.content()
                ))
                .toList();
    }
}
