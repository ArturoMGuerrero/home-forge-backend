package com.casaflow.appointment.dto;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentOutcome;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.casaflow.appointment.domain.AppointmentType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID companyId,
        String title,
        String description,
        AppointmentType appointmentType,
        AppointmentStatus status,

        @JsonProperty("startsAt")
        Instant startTime,

        @JsonProperty("endsAt")
        Instant endTime,

        String timezone,
        Boolean allDay,
        UUID assignedUserId,
        UUID leadId,
        UUID propertyId,

        @JsonProperty("location")
        String locationAddress,

        String virtualMeetingUrl,
        Integer reminderMinutes,
        Boolean reminderSent,
        Instant reminderSentAt,
        String googleCalendarEventId,
        Boolean googleCalendarSyncEnabled,
        Instant lastSyncedAt,
        String notes,
        AppointmentOutcome outcome,
        Boolean followUpRequired,
        String metadata,
        UUID createdByUserId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getCompanyId(),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getTimezone(),
                appointment.getAllDay(),
                appointment.getAssignedUserId(),
                appointment.getLeadId(),
                appointment.getPropertyId(),
                appointment.getLocationAddress(),
                appointment.getVirtualMeetingUrl(),
                appointment.getReminderMinutes(),
                appointment.getReminderSent(),
                appointment.getReminderSentAt(),
                appointment.getGoogleCalendarEventId(),
                appointment.getGoogleCalendarSyncEnabled(),
                appointment.getLastSyncedAt(),
                appointment.getNotes(),
                appointment.getOutcome(),
                appointment.getFollowUpRequired(),
                appointment.getMetadata(),
                appointment.getCreatedByUserId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getDeletedAt()
        );
    }
}
