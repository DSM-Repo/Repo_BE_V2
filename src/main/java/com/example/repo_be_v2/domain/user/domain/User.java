package com.example.repo_be_v2.domain.user.domain;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false , name = "student_name" , length = 4)
    private String studentName;

    @Column(nullable = false, unique = true, name = "student_email")
    @Email
    private String studentEmail;

    @Column(name = "student_grade", nullable = false)
    private Integer studentGrade;

    @Column(name = "student_class",nullable = false)
    private Integer studentClass;

    @Column(name = "student_number", nullable = false)
    private Integer studentNumber;

    @Column(name = "student_password", nullable = false)
    private String studentPassword;

    //전공은 카탈로그(tbl_major)에서 고른 값이다. 아직 고르지 않았으면 null이다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //전공을 고르지 않은 사용자도 있으므로 이름은 없을 수 있다.
    public String getMajorName() {
        return major == null ? null : major.getName();
    }
}
