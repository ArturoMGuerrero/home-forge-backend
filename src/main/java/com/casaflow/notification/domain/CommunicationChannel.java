package com.casaflow.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "communication_channels")
public class CommunicationChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private NotificationType channelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunicationProvider provider;

    @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String configuration; // JSON encriptado

    private Boolean active = true;
    private Boolean verified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "monthly_limit")
    private Integer monthlyLimit;

    @Column(name = "daily_sent", nullable = false)
    private Integer dailySent = 0;

    @Column(name = "monthly_sent", nullable = false)
    private Integer monthlySent = 0;

    @Column(name = "last_reset_daily")
    private Instant lastResetDaily;

    @Column(name = "last_reset_monthly")
    private Instant lastResetMonthly;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String metadata; // JSON

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public NotificationType getChannelType() {
        return channelType;
    }

    public void setChannelType(NotificationType channelType) {
        this.channelType = channelType;
    }

    public CommunicationProvider getProvider() {
        return provider;
    }

    public void setProvider(CommunicationProvider provider) {
        this.provider = provider;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Integer getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Integer monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Integer getDailySent() {
        return dailySent;
    }

    public void setDailySent(Integer dailySent) {
        this.dailySent = dailySent;
    }

    public Integer getMonthlySent() {
        return monthlySent;
    }

    public void setMonthlySent(Integer monthlySent) {
        this.monthlySent = monthlySent;
    }

    public Instant getLastResetDaily() {
        return lastResetDaily;
    }

    public void setLastResetDaily(Instant lastResetDaily) {
        this.lastResetDaily = lastResetDaily;
    }

    public Instant getLastResetMonthly() {
        return lastResetMonthly;
    }

    public void setLastResetMonthly(Instant lastResetMonthly) {
        this.lastResetMonthly = lastResetMonthly;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
