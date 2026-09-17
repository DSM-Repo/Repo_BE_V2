package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeAutoSaveResponse;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeAutoSaveService {

    private final ResumeSaveService resumeSaveService;

    /**
     * 이력서 자동 저장
     *
     * 저장하는 내용은 수동 저장과 같다.
     * 첫 진입한 학생은 저장 버튼을 누르지 않고 쓰기 시작하므로
     * 이력서가 없으면 자동 저장이 만들어 준다.
     * 엔드포인트를 따로 두는 이유는 응답으로 자동 저장임을 알려주기 위해서다.
     */
    @Transactional
    public ResumeAutoSaveResponse execute(Long userId, ResumeSaveRequest request) {
        ResumeSaveResponse saved = resumeSaveService.execute(userId, request);

        return new ResumeAutoSaveResponse(
                saved.resumeId(),
                saved.savedAt(),
                true
        );
    }
}
