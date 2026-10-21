package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
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
        ResumeSubmissionStatus readStatus = resume.getSubmissionStatus();

        resume.changeVisibility(request.isPublic(), LocalDateTime.now());

        /*
         * 제출한 이력서도 학생이 계속 저장할 수 있다.
         * 문서를 통째로 저장하면 그 사이 학생이 저장한 본문을 여기서 읽어둔 옛 본문으로 되돌린다.
         * 그래서 상태 관련 필드만, 읽은 뒤 상태가 그대로일 때만 바꾼다.
         * 그 사이 학생이 제출을 취소했으면 공개하면 안 되므로 밀어낸다.
         */
        Resume savedResume = resumeRepository.updateStatusIfUnchanged(resume, readStatus)
                .orElseThrow(() -> resumeReader.statusChangedException(studentId));

        return new ResumeVisibilityResponse(savedResume.isPublic());
    }
}
