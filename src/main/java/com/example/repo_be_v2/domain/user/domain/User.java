package com.example.repo_be_v2.domain.user.domain;

import com.example.repo_be_v2.domain.user.domain.enums.Role;
import jakarta.persistence.*;
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
    @Column(nullable = false, name = "student_id")
    private Long id;

    @Column(nullable = false , name = "student_name" , length = 4)
    private String studentName;

    @Column(name = "student_id",nullable = false, unique = true)
    private String studentId;

    @Column(name = "student_password", nullable = false)
    private String studentPassword;

    @Column(name = "student_major", nullable = false)
    private String studentMajor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


}
