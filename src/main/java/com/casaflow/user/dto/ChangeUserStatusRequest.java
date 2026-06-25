package com.casaflow.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeUserStatusRequest(
        @NotNull UUID companyId,
        @NotNull UUID requesterUserId,
        @NotNull Boolean active
) {
}
