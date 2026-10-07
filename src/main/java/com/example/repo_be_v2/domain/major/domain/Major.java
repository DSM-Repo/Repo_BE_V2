package com.example.repo_be_v2.domain.major.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 학생이 고를 수 있는 전공 카탈로그.
 *
 * 교사가 추가·삭제하고 학생은 목록에서 고른다.
 * 이름을 유일 제약으로 막아 같은 전공이 두 번 등록되지 않게 한다.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_major")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    /**
     * 전공을 추가한 시각. 전공 관리 화면에 생성일로 보여준다.
     *
     * 컬럼을 뒤늦게 붙인 터라 그 전에 만들어진 전공은 비어 있다.
     * NOT NULL로 두면 기존 행이 있는 테이블에 컬럼을 붙이지 못하므로 null을 허용한다.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static Major create(String name) {
        return Major.builder()
                .name(name)
                .build();
    }
}
