package com.example.repo_be_v2.domain.major.service;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.major.domain.repository.MajorRepository;
import com.example.repo_be_v2.domain.major.presentation.dto.request.MajorCreateRequest;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorResponse;
import com.example.repo_be_v2.domain.major.service.support.MajorReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MajorCreateService {

    private final MajorRepository majorRepository;
    private final MajorReader majorReader;

    /**
     * 전공 추가 (선생님 권한)
     *
     * 이름 양끝 공백은 서버가 정리한다.
     * "백엔드"와 "백엔드 "가 다른 전공으로 등록되면 중복 판정이 무너지기 때문이다.
     */
    @Transactional
    public MajorResponse execute(Long teacherId, MajorCreateRequest request) {
        majorReader.getTeacher(teacherId);

        String name = request.name().trim();
        majorReader.validateNameNotDuplicated(name);

        Major major = majorRepository.save(Major.create(name));

        return MajorResponse.from(major);
    }
}
