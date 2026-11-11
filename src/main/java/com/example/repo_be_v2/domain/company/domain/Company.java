package com.example.repo_be_v2.domain.company.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 도서관만 열람할 수 있는 기업 계정.
 *
 * 선생님이 회사마다 하나씩 발급하고, 필요 없어지면 삭제한다. 기업이 스스로 가입하는 길은 없다.
 *
 * 학생·선생님(tbl_user)과 테이블을 나눈 이유는 두 가지다.
 * 학년·반·번호처럼 기업에게는 없는 값이 tbl_user에서 NOT NULL이고,
 * 학생 목록·전공 인원처럼 tbl_user를 훑는 조회에 기업이 섞여 들어갈 일을 아예 없애기 위해서다.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    //로그인에 쓰는 이메일. 학교 이메일(dsm.hs.kr)은 받지 않아 tbl_user의 이메일과 겹치지 않는다.
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Company create(String name, String email, String encodedPassword) {
        return Company.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
    }
}
