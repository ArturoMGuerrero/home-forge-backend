package com.casaflow.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateTeamRequest(
        @NotNull UUID companyId,
        @NotNull UUID requesterUserId,
        @NotBlank @Size(max = 120) String name,
        String description,
        UUID leaderId,
        List<UUID> memberIds
) {
}
