package com.example.repo_be_v2.domain.major.service;

import com.example.repo_be_v2.domain.major.domain.repository.MajorRepository;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorListResponse;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorListService {

    private final MajorRepository majorRepository;

    /**
     * 전공 목록 조회 (인증된 사용자)
     *
     * 학생이 전공을 고르는 화면과 교사의 전공 관리 화면이 같은 목록을 쓴다.
     * 화면에서 정렬을 다시 하지 않도록 이름 오름차순으로 내려준다.
     */
    @Transactional(readOnly = true)
    public MajorListResponse execute() {
        List<MajorResponse> majors = majorRepository.findAllByOrderByNameAsc()
                .stream()
                .map(MajorResponse::from)
                .toList();

        return new MajorListResponse(majors, majors.size());
    }
}
