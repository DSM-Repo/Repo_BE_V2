package com.example.repo_be_v2.domain.major.service;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.major.domain.repository.MajorRepository;
import com.example.repo_be_v2.domain.major.service.support.MajorReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MajorDeleteService {

    private final MajorRepository majorRepository;
    private final MajorReader majorReader;

    /**
     * 전공 삭제 (선생님 권한)
     *
     * 학생이 쓰고 있는 전공은 지우지 않는다.
     * 지워버리면 그 학생들의 전공이 한꺼번에 비어버리고, 되돌릴 방법이 없다.
     */
    @Transactional
    public void execute(Long teacherId, Long majorId) {
        majorReader.getTeacher(teacherId);

        Major major = majorReader.getMajor(majorId);
        majorReader.validateNotInUse(major.getId());

        majorRepository.delete(major);
    }
}
