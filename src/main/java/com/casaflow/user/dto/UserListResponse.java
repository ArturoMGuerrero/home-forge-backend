package com.casaflow.user.dto;

import java.util.List;

public record UserListResponse(
        String planCode,
        int userLimit,
        long usedSeats,
        List<UserResponse> users
) {
}
