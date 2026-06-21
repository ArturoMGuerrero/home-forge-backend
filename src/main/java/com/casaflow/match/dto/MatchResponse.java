package com.casaflow.match.dto;

import com.casaflow.match.domain.LeadPropertyMatch;

import java.time.Instant;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        UUID leadId,
        String leadName,
        UUID propertyId,
        String propertyTitle,
        String propertyCode,
        String status,
        String notes,
        Instant createdAt
) {
    public static MatchResponse of(LeadPropertyMatch match, String leadName, String propertyTitle, String propertyCode) {
        return new MatchResponse(match.getId(), match.getLeadId(), leadName, match.getPropertyId(),
                propertyTitle, propertyCode, match.getStatus(), match.getNotes(), match.getCreatedAt());
    }
}
