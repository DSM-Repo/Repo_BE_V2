package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;

public record UserResponse(
        Long id,
        String studentName,
<<<<<<< HEAD
        String studentEmail,
        Integer studentGrade,
        Integer studentClass,
        Integer studentNumber,
=======
        String studentId,
>>>>>>> origin/1-feat-user-login
        String studentMajor,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getStudentName(),
<<<<<<< HEAD
                user.getStudentEmail(),
                user.getStudentGrade(),
                user.getStudentClass(),
                user.getStudentNumber(),
=======
                user.getStudentId(),
>>>>>>> origin/1-feat-user-login
                user.getStudentMajor(),
                user.getRole()
        );
    }
}
