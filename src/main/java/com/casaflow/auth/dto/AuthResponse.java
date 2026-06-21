package com.casaflow.auth.dto;

import java.util.UUID;
import java.time.Instant;

public record AuthResponse(
        UUID userId,
        UUID companyId,
        String name,
        String companyName,
        String email,
        String role,
        String planCode,
        int userLimit,
        String subscriptionStatus,
        Instant trialEndsAt
) {
}
