package com.casaflow.lead.dto;

import com.casaflow.lead.domain.FollowUpTaskPriority;
import com.casaflow.lead.domain.FollowUpTaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record UpdateFollowUpTaskRequest(
        @NotNull UUID companyId,
        @Size(max = 255) String title,
        @Size(max = 5000) String description,
        FollowUpTaskStatus status,
        Instant scheduledFor,
        UUID assignedToUserId,
        FollowUpTaskPriority priority
) {
}
