package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ResumeRepository
        extends MongoRepository<Resume, String> {

    Optional<Resume> findByUserId(Long userId);

    Optional<Resume> findByIdAndUserId(
            String id,
            Long userId
    );

    //도서관은 공개된 이력서만 다룬다. 공개 여부가 곧 도서관 등록 여부다.
    Optional<Resume> findByUserIdAndIsPublicTrue(Long userId);

    //목록·검색용. 본문은 필요 없어서 요약만 받는다.
    List<PublicResumeSummary> findByIsPublicTrue();

    /**
     * 특정 학년도에 공개된 이력서만 조회한다.
     *
     * 학년도는 releasedAt에서 계산하는 값이라 저장돼 있지 않다.
     * 그래서 연도 자체로 거르지 않고 그 학년도의 시작부터 다음 학년도 시작 직전까지로 조회한다.
     *
     * 메서드 이름으로 파생시키면 같은 필드에 $gte와 $lt를 나눠 걸게 되는데,
     * Spring Data MongoDB가 이 둘을 한 조건으로 합치지 못해 실행 시점에 터진다.
     * 그래서 쿼리를 직접 적는다.
     */
    @Query(
            value = "{ 'isPublic': true, 'releasedAt': { $gte: ?0, $lt: ?1 } }",
            fields = "{ 'userId': 1, 'releasedAt': 1 }"
    )
    List<PublicResumeSummary> findPublicReleasedIn(
            LocalDateTime start,
            LocalDateTime end
    );
}
