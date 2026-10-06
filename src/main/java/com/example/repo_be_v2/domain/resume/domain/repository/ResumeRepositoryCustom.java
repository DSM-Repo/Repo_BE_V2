package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;

import java.util.Optional;

/**
 * 조건을 건 쓰기. 파생 메서드로는 표현할 수 없어 MongoTemplate으로 따로 구현한다.
 *
 * 둘 다 저장된 상태가 아직 학생이 손댈 수 있을 때만 바꾸고,
 * 그 사이에 상태가 바뀌었으면 비어 있는 값을 돌려준다.
 *
 * 각자 자기가 책임지는 필드만 건드린다.
 * 문서를 통째로 바꾸면 저장과 제출이 겹쳤을 때
 * 늦게 끝난 저장이 들고 있던 옛 상태로 제출을 되돌려버린다.
 */
public interface ResumeRepositoryCustom {

    //본문만 갱신한다. 제출 상태는 건드리지 않는다.
    Optional<Resume> updateContentIfWritable(Resume resume);

    //본문과 함께 제출 상태·제출 시각까지 갱신한다.
    Optional<Resume> submitIfWritable(Resume resume);
}
