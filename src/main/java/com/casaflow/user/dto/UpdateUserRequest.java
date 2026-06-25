package com.casaflow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateUserRequest(
        UUID companyId,
        UUID requesterUserId,
        @Size(min = 2, max = 180) String fullName,
        @Email String email,
        @Pattern(regexp = "^\\+[1-9][0-9]{1,14}$", message = "Phone must be in E.164 format")
        String phoneE164
) {
}
