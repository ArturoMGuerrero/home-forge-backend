package com.casaflow.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MatchRequest(
        @NotNull UUID companyId,
        @NotNull UUID leadId,
        @NotNull UUID propertyId,
        @Pattern(regexp = "SUGGESTED|SENT|INTERESTED|VISIT_SCHEDULED|REJECTED") String status,
        @Size(max = 3000) String notes
) {}
