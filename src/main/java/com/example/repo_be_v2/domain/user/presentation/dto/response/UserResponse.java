package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;

public record UserResponse(
        Long id,
        String studentName,
        String studentId,
        String studentMajor,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getStudentName(),
                user.getStudentId(),
                user.getStudentMajor(),
                user.getRole()
        );
    }
}
