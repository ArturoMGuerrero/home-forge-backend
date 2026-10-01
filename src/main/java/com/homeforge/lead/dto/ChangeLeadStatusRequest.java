package com.homeforge.lead.dto;

import com.homeforge.lead.domain.LeadStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeLeadStatusRequest(
        @NotNull UUID companyId,
        @NotNull LeadStatus status
) {
}
