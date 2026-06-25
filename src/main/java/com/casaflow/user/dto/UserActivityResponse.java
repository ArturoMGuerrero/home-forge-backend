package com.casaflow.user.dto;

import com.casaflow.user.domain.UserActivity;

import java.time.Instant;
import java.util.UUID;

public record UserActivityResponse(
        UUID id,
        UUID userId,
        String activityType,
        String activityCategory,
        String entityType,
        UUID entityId,
        String descriptionEn,
        String descriptionEs,
        Instant createdAt
) {
    public static UserActivityResponse from(UserActivity activity) {
        return new UserActivityResponse(
                activity.getId(),
                activity.getUserId(),
                activity.getActivityType().name(),
                activity.getActivityCategory().name(),
                activity.getEntityType(),
                activity.getEntityId(),
                activity.getDescriptionEn(),
                activity.getDescriptionEs(),
                activity.getCreatedAt()
        );
    }
}
