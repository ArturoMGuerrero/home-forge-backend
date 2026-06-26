package com.casaflow.lead.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lead_score_history")
public class LeadScoreHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID leadId;

    @Column(nullable = false)
    private int oldScore;

    @Column(nullable = false)
    private int newScore;

    private String reason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected LeadScoreHistory() {}

    public LeadScoreHistory(UUID companyId, UUID leadId, int oldScore, int newScore, String reason) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.oldScore = oldScore;
        this.newScore = newScore;
        this.reason = reason;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public int getOldScore() { return oldScore; }
    public int getNewScore() { return newScore; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }
}
