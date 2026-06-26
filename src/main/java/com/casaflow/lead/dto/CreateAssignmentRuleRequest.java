package com.casaflow.lead.dto;

import com.casaflow.lead.domain.AssignmentStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAssignmentRuleRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 5000) String description,
        int priority,
        @NotNull AssignmentStrategy assignmentStrategy,
        String criteriaSource,
        String criteriaListingType,
        String criteriaCity,
        BigDecimal criteriaBudgetMin,
        BigDecimal criteriaBudgetMax,
        String criteriaPropertyType,
        @NotBlank String assignedUserIds
) {
}
