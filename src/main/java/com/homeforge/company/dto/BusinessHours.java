package com.homeforge.company.dto;

import com.homeforge.company.domain.Company;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Horario de atención de la empresa: de qué hora a qué hora se agendan citas y cada cuántos minutos. */
public record BusinessHours(
        @NotNull @Pattern(regexp = TIME, message = "Usa el formato HH:mm") String start,
        @NotNull @Pattern(regexp = TIME, message = "Usa el formato HH:mm") String end,
        @NotNull Integer slotMinutes
) {

    private static final String TIME = "([01]\\d|2[0-3]):[0-5]\\d";

    public static BusinessHours from(Company company) {
        return new BusinessHours(
                company.getBusinessHoursStart().toString(),
                company.getBusinessHoursEnd().toString(),
                company.getAppointmentSlotMinutes()
        );
    }
}
