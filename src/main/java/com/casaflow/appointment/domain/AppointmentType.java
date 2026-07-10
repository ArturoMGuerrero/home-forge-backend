package com.casaflow.appointment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentType {
    PROPERTY_TOUR,
    MEETING,
    CALL,
    VIDEO_CALL,
    SIGNING,
    OTHER;

    @JsonCreator
    public static AppointmentType fromValue(String value) {
        // Mapear "TOUR" a "PROPERTY_TOUR" para retrocompatibilidad
        if ("TOUR".equalsIgnoreCase(value)) {
            return PROPERTY_TOUR;
        }
        return valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
