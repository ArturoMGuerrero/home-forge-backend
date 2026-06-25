package com.casaflow.user.dto;

public record UpdateUserSettingsRequest(
        String language,
        String timezone,
        String currency,
        Boolean emailNotifications,
        Boolean pushNotifications,
        Boolean notificationNewLead,
        Boolean notificationLeadUpdate,
        Boolean notificationAppointment,
        Boolean notificationTeamActivity,
        String theme,
        String emailSignature,
        String dashboardLayout
) {
}
