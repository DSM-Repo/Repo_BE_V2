package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;

public record UserResponse(
        Long id,
        String studentName,
        String studentEmail,
        Integer studentGrade,
        Integer studentClass,
        Integer studentNumber,
        String studentMajor,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getStudentName(),
                user.getStudentEmail(),
                user.getStudentGrade(),
                user.getStudentClass(),
                user.getStudentNumber(),
                user.getStudentMajor(),
                user.getRole()
        );
    }
}
