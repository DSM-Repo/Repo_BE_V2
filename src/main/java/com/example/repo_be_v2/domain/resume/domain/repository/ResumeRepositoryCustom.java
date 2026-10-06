package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;

import java.util.Optional;

/**
 * 조건을 건 쓰기. 파생 메서드로는 표현할 수 없어 MongoTemplate으로 따로 구현한다.
 */
public interface ResumeRepositoryCustom {

    /**
     * 저장된 상태가 아직 제출 가능할 때만 이력서를 통째로 바꾼다.
     *
     * 읽을 때 제출 가능했어도 쓰기 직전에 선생님이 공개로 바꿔버릴 수 있다.
     * 그 사이에 save()로 덮어쓰면 공개 상태가 통째로 사라지므로,
     * 조건 검사와 교체를 한 번의 원자적 연산으로 처리한다.
     *
     * 조건에 맞는 문서가 없으면(= 그 사이에 상태가 바뀌었으면) 비어 있는 값을 돌려준다.
     */
    Optional<Resume> replaceIfSubmittable(Resume resume);
}
