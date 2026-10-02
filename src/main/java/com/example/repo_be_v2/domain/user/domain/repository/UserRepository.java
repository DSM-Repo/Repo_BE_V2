package com.example.repo_be_v2.domain.user.domain.repository;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByStudentEmail(String studentEmail);

    Optional<User> findByStudentEmail(String studentEmail);

    //전공 삭제 전 사용 중인지 판정한다.
    long countByMajorId(Long majorId);

    /**
     * 도서관 검색.
     *
     * 공개 여부는 MongoDB(resumes)에 있고 이름·전공은 MySQL에 있어서 한 쿼리로 묶을 수 없다.
     * 그래서 공개된 이력서의 userId를 먼저 구해 ids로 넘기고, 여기서 이름·전공 필터와 페이징을 처리한다.
     * 페이징을 한쪽 저장소가 맡아야 totalElements가 정확해진다.
     *
     * 전공 이름을 응답에 담아야 하므로 major를 fetch join으로 같이 가져와 N+1을 막는다.
     */
    @Query(
            value = """
                    select u from User u
                    left join fetch u.major m
                    where u.id in :ids
                      and (:keyword is null or u.studentName like concat('%', :keyword, '%'))
                      and (:majorName is null or m.name = :majorName)
                    order by u.studentName asc
                    """,
            countQuery = """
                    select count(u) from User u
                    left join u.major m
                    where u.id in :ids
                      and (:keyword is null or u.studentName like concat('%', :keyword, '%'))
                      and (:majorName is null or m.name = :majorName)
                    """
    )
    Page<User> searchInIds(
            @Param("ids") Collection<Long> ids,
            @Param("keyword") String keyword,
            @Param("majorName") String majorName,
            Pageable pageable
    );

    /**
     * 선생님이 보는 학생 목록. 학년·반을 주면 그 반만, 비우면 전교생이다.
     *
     * 선생님 계정도 같은 테이블에 있어서 role로 학생만 거른다.
     * 학번순(학년·반·번호)으로 정렬해 화면 순서를 그대로 쓸 수 있게 한다.
     */
    @Query("""
            select u from User u
            left join fetch u.major
            where u.role = :role
              and (:grade is null or u.studentGrade = :grade)
              and (:classNumber is null or u.studentClass = :classNumber)
            order by u.studentGrade asc, u.studentClass asc, u.studentNumber asc
            """)
    List<User> findStudents(
            @Param("role") Role role,
            @Param("grade") Integer grade,
            @Param("classNumber") Integer classNumber
    );

    /**
     * 전공 관리 화면이 쓰는 전공별 학생 목록. 학년·반을 주면 그 안에서만 고른다.
     *
     * 전공을 고르지 않은 학생도 같은 테이블에 있어서 major_id로 거른다.
     * 정렬과 role 조건은 findStudents와 같아 화면 순서를 그대로 쓸 수 있다.
     */
    @Query("""
            select u from User u
            join fetch u.major m
            where u.role = :role
              and m.id = :majorId
              and (:grade is null or u.studentGrade = :grade)
              and (:classNumber is null or u.studentClass = :classNumber)
            order by u.studentGrade asc, u.studentClass asc, u.studentNumber asc
            """)
    List<User> findStudentsByMajorId(
            @Param("role") Role role,
            @Param("majorId") Long majorId,
            @Param("grade") Integer grade,
            @Param("classNumber") Integer classNumber
    );

    /**
     * 여러 사용자를 전공까지 함께 가져온다.
     *
     * 전공은 지연 로딩이라 findAllById로 받아오면 이름을 읽을 때마다 조회가 따로 나간다.
     * 도서관처럼 여러 명의 전공을 한꺼번에 쓰는 곳에서 쓴다.
     */
    @Query("""
            select u from User u
            left join fetch u.major
            where u.id in :ids
            """)
    List<User> findAllWithMajorByIdIn(@Param("ids") Collection<Long> ids);
}
