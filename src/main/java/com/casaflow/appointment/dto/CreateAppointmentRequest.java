package com.casaflow.appointment.dto;

import com.casaflow.appointment.domain.AppointmentType;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull UUID companyId,
        @NotBlank String title,
        String description,
        @NotNull AppointmentType appointmentType,
        AppointmentStatus status,

        @JsonAlias({"startsAt", "start_time"})
        @NotNull Instant startTime,

        @JsonAlias({"endsAt", "end_time"})
        @NotNull Instant endTime,

        String timezone,
        Boolean allDay,
        UUID assignedUserId,
        UUID leadId,
        UUID propertyId,

        @JsonAlias({"location"})
        String locationAddress,

        String virtualMeetingUrl,
        Integer reminderMinutes,
        String notes,
        UUID createdByUserId
) {
}
