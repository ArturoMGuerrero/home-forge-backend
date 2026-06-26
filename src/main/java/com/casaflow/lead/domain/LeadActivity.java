package com.casaflow.lead.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lead_activities")
public class LeadActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID leadId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private LeadActivityType activityType;

    @Column(nullable = false, columnDefinition = "text")
    private String notes;

    @Column(nullable = false)
    private Instant occurredAt;

    private Instant nextFollowUpAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private UUID userId;

    private Integer durationMinutes;

    @Column(length = 50)
    private String outcome;

    private UUID propertyId;

    @Column(columnDefinition = "text")
    private String attachments;

    @Column(columnDefinition = "text")
    private String metadata;

    protected LeadActivity() {
    }

    public LeadActivity(
            UUID companyId,
            UUID leadId,
            LeadActivityType activityType,
            String notes,
            Instant occurredAt,
            Instant nextFollowUpAt
    ) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.activityType = activityType;
        this.notes = notes;
        this.occurredAt = occurredAt;
        this.nextFollowUpAt = nextFollowUpAt;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public LeadActivityType getActivityType() { return activityType; }
    public String getNotes() { return notes; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getNextFollowUpAt() { return nextFollowUpAt; }
    public Instant getCreatedAt() { return createdAt; }
    public UUID getUserId() { return userId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public String getOutcome() { return outcome; }
    public UUID getPropertyId() { return propertyId; }
    public String getAttachments() { return attachments; }
    public String getMetadata() { return metadata; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }
    public void setAttachments(String attachments) { this.attachments = attachments; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
