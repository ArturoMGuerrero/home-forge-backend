package com.casaflow.lead.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAssignmentRuleRequest(
        @NotNull UUID companyId,
        Boolean active,
        Integer priority
) {
}
