package com.example.repo_be_v2.domain.major.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학생이 고를 수 있는 전공 카탈로그.
 *
 * 교사가 추가·삭제하고 학생은 목록에서 고른다.
 * 이름을 유일 제약으로 막아 같은 전공이 두 번 등록되지 않게 한다.
 */
@Entity
@Table(name = "tbl_major")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    public static Major create(String name) {
        Major major = new Major();

        major.name = name;

        return major;
    }
}
