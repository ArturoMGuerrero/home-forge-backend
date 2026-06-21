package com.casaflow.match.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "lead_property_matches")
public class LeadPropertyMatch extends AuditableEntity {
    @Column(nullable = false) private UUID companyId;
    @Column(nullable = false) private UUID leadId;
    @Column(nullable = false) private UUID propertyId;
    @Column(nullable = false, length = 30) private String status = "SUGGESTED";
    @Column(columnDefinition = "text") private String notes;

    protected LeadPropertyMatch() {}

    public LeadPropertyMatch(UUID companyId, UUID leadId, UUID propertyId, String status, String notes) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.propertyId = propertyId;
        this.status = status;
        this.notes = notes;
    }

    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public UUID getPropertyId() { return propertyId; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public void update(String status, String notes) { this.status = status; this.notes = notes; }
}
