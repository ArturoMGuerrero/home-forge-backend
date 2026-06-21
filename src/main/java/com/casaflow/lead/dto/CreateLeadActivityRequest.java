package com.casaflow.lead.dto;

import com.casaflow.lead.domain.LeadActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateLeadActivityRequest(
        @NotNull UUID companyId,
        @NotNull LeadActivityType activityType,
        @NotBlank @Size(max=5000) String notes,
        Instant occurredAt,
        Instant nextFollowUpAt
) {
}
