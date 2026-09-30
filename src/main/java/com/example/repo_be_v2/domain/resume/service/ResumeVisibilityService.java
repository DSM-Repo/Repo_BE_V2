package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeVisibilityResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeVisibilityService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    /**
     * 학생 이력서 공개 여부 변경 (선생님 권한)
     *
     * 공개는 선생님이 확인을 마친 이력서를 도서관에 올리는 일이라 학생 본인은 할 수 없다.
     * 도서관은 isPublic만 보고 목록을 만들므로 여기서 공개하면 바로 등록된다.
     */
    @Transactional
    public ResumeVisibilityResponse execute(Long teacherId, Long studentId, ResumeVisibilityRequest request) {
        resumeReader.getTeacher(teacherId);

        Resume resume = resumeReader.getStudentResume(studentId);

        resume.changeVisibility(request.isPublic(), LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeVisibilityResponse(savedResume.isPublic());
    }
}
