package com.casaflow.agenda.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment extends AuditableEntity {
    @Column(nullable = false) private UUID companyId;
    private UUID leadId;
    private UUID propertyId;
    @Column(nullable = false, length = 180) private String title;
    @Column(nullable = false, length = 40) private String appointmentType;
    @Column(nullable = false, length = 30) private String status = "SCHEDULED";
    @Column(nullable = false) private Instant startsAt;
    private Instant endsAt;
    @Column(length = 255) private String location;
    @Column(columnDefinition = "text") private String notes;

    protected Appointment() {}

    public Appointment(UUID companyId, UUID leadId, UUID propertyId, String title, String appointmentType,
                       String status, Instant startsAt, Instant endsAt, String location, String notes) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.propertyId = propertyId;
        this.title = title;
        this.appointmentType = appointmentType;
        this.status = status;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
        this.notes = notes;
    }

    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public UUID getPropertyId() { return propertyId; }
    public String getTitle() { return title; }
    public String getAppointmentType() { return appointmentType; }
    public String getStatus() { return status; }
    public Instant getStartsAt() { return startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public String getLocation() { return location; }
    public String getNotes() { return notes; }

    public void update(UUID leadId, UUID propertyId, String title, String appointmentType, String status,
                       Instant startsAt, Instant endsAt, String location, String notes) {
        this.leadId = leadId;
        this.propertyId = propertyId;
        this.title = title;
        this.appointmentType = appointmentType;
        this.status = status;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
        this.notes = notes;
    }
}
