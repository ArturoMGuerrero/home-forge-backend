package com.casaflow.user.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 60)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_category", nullable = false, length = 40)
    private ActivityCategory activityCategory;

    @Column(name = "entity_type", length = 40)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "description_en", length = 255)
    private String descriptionEn;

    @Column(name = "description_es", length = 255)
    private String descriptionEs;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected UserActivity() {
    }

    public UserActivity(
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
        this.companyId = companyId;
        this.userId = userId;
        this.activityType = activityType;
        this.activityCategory = activityCategory;
        this.entityType = entityType;
        this.entityId = entityId;
        this.descriptionEn = descriptionEn;
        this.descriptionEs = descriptionEs;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.metadata = metadata;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getUserId() {
        return userId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public ActivityCategory getActivityCategory() {
        return activityCategory;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionEs() {
        return descriptionEs;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getMetadata() {
        return metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
