package com.casaflow.user.dto;

import com.casaflow.user.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phoneE164,
        String role,
        boolean active
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneE164(),
                user.getRole(),
                user.isActive()
        );
    }
}
