package com.casaflow.user.service;

import com.casaflow.user.domain.*;
import com.casaflow.user.repository.UserActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Transactional
    public void log(
            UUID companyId,
            UUID userId,
            ActivityType activityType,
            ActivityCategory activityCategory,
            String entityType,
            UUID entityId,
            String descriptionEn,
            String descriptionEs
    ) {
        log(companyId, userId, activityType, activityCategory, entityType, entityId, descriptionEn, descriptionEs, null, null, null);
    }

    @Transactional
    public void log(
            UUID companyId,
            UUID userId,
            ActivityType activityType,
            ActivityCategory activityCategory,
            String entityType,
            UUID entityId,
            String descriptionEn,
            String descriptionEs,
            String ipAddress,
            String userAgent,
            String metadata
    ) {
        UserActivity activity = new UserActivity(
                companyId,
                userId,
                activityType,
                activityCategory,
                entityType,
                entityId,
                descriptionEn,
                descriptionEs,
                ipAddress,
                userAgent,
                metadata
        );
        userActivityRepository.save(activity);
    }
}
