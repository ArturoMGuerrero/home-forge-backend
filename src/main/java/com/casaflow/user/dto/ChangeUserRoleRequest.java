package com.casaflow.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeUserRoleRequest(
        @NotNull UUID companyId,
        @NotNull UUID requesterUserId,
        @NotBlank String role,
        UUID roleId
) {
}
