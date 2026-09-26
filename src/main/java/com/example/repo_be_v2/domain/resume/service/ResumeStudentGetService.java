package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeStudentGetService {

    private final ResumeReader resumeReader;

    /**
     * 학생 이력서 조회 (선생님 권한)
     *
     * 피드백을 달 화면에서 이력서 본문을 띄우기 위한 조회다.
     * 좌표를 찍으려면 페이지를 봐야 하므로 작성 중인 이력서도 포함한다.
     *
     * 응답 형식은 학생 본인이 보는 것과 같다. 같은 화면을 그대로 쓰기 위해서다.
     */
    @Transactional(readOnly = true)
    public ResumeResponse execute(Long teacherId, Long studentId) {
        resumeReader.getTeacher(teacherId);

        User student = resumeReader.getUser(studentId);
        Resume resume = resumeReader.getStudentResume(studentId);

        return ResumeResponse.of(resume, student);
    }
}
