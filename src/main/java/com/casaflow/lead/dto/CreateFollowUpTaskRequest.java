package com.casaflow.lead.dto;

import com.casaflow.lead.domain.FollowUpTaskPriority;
import com.casaflow.lead.domain.FollowUpTaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateFollowUpTaskRequest(
        @NotNull UUID companyId,
        @NotNull UUID leadId,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 5000) String description,
        @NotNull FollowUpTaskType taskType,
        @NotNull Instant scheduledFor,
        UUID assignedToUserId,
        FollowUpTaskPriority priority
) {
}
