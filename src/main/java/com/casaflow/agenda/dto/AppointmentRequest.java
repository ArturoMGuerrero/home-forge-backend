package com.casaflow.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID companyId,
        UUID leadId,
        UUID propertyId,
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Pattern(regexp = "CALL|MEETING|TOUR|FOLLOW_UP|OTHER") String appointmentType,
        @NotBlank @Pattern(regexp = "SCHEDULED|COMPLETED|CANCELED") String status,
        @NotNull Instant startsAt,
        Instant endsAt,
        @Size(max = 255) String location,
        @Size(max = 5000) String notes
) {}
