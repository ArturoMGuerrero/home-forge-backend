package com.casaflow.subscription.dto;

import com.casaflow.company.domain.Company;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(
        UUID companyId,
        String planCode,
        int userLimit,
        String status,
        Instant trialStartedAt,
        Instant trialEndsAt,
        long trialDaysRemaining,
        Instant nextBillingAt,
        boolean paymentConfigured
) {
    public static SubscriptionResponse from(Company company) {
        Instant now = Instant.now();
        long days = company.getTrialEndsAt() == null || !company.getTrialEndsAt().isAfter(now)
                ? 0
                : Math.max(1, (long) Math.ceil(Duration.between(now, company.getTrialEndsAt()).toMinutes() / 1440.0));
        return new SubscriptionResponse(
                company.getId(),
                company.getPlanCode().name(),
                company.getPlanCode().getUserLimit(),
                company.getSubscriptionStatus(),
                company.getTrialStartedAt(),
                company.getTrialEndsAt(),
                days,
                company.getNextBillingAt(),
                false
        );
    }
}
