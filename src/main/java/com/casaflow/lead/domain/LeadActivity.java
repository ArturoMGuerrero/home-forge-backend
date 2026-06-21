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
}
