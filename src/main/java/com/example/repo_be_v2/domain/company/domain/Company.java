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
 * 선생님이 회사 이름을 넣어 만들면 인증 코드가 하나 발급되고, 기업은 그 코드만 입력해 로그인한다.
 * 이메일도 비밀번호도 없고 코드가 곧 계정이다. 기업이 스스로 가입하는 길은 없다.
 * 기한은 따로 없어서, 선생님이 계정을 삭제할 때까지 같은 코드를 계속 쓴다.
 *
 * 학생·선생님(tbl_user)과 테이블을 나눈 이유는 두 가지다.
 * 이메일·학년·반·번호처럼 기업에게는 없는 값이 tbl_user에서 NOT NULL이고,
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

    //목록에서 어느 회사의 코드인지 가려내는 값이라 겹치지 않게 한다.
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 인증 코드. 구분선 없이 16자로 저장한다. (CompanyCode)
     *
     * 선생님이 목록에서 언제든 다시 확인해 전달할 수 있어야 해서 해시하지 않고 그대로 둔다.
     */
    @Column(name = "code", nullable = false, unique = true, length = CompanyCode.LENGTH)
    private String code;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Company create(String name, String code) {
        return Company.builder()
                .name(name)
                .code(code)
                .build();
    }
}
