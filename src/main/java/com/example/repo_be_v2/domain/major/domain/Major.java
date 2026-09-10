package com.example.repo_be_v2.domain.major.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 학생이 고를 수 있는 전공 카탈로그.
 *
 * 교사가 추가·삭제하고 학생은 목록에서 고른다.
 * 이름을 유일 제약으로 막아 같은 전공이 두 번 등록되지 않게 한다.
 */
@Entity
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

    public static Major create(String name) {
        return Major.builder()
                .name(name)
                .build();
    }
}
