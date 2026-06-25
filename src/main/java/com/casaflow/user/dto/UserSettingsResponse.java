package com.casaflow.user.dto;

import com.casaflow.user.domain.UserSettings;

public record UserSettingsResponse(
        String language,
        String timezone,
        String currency,
        boolean emailNotifications,
        boolean pushNotifications,
        boolean notificationNewLead,
        boolean notificationLeadUpdate,
        boolean notificationAppointment,
        boolean notificationTeamActivity,
        String theme,
        String emailSignature,
        String dashboardLayout
) {
    public static UserSettingsResponse from(UserSettings settings) {
        return new UserSettingsResponse(
                settings.getLanguage(),
                settings.getTimezone(),
                settings.getCurrency(),
                settings.isEmailNotifications(),
                settings.isPushNotifications(),
                settings.isNotificationNewLead(),
                settings.isNotificationLeadUpdate(),
                settings.isNotificationAppointment(),
                settings.isNotificationTeamActivity(),
                settings.getTheme(),
                settings.getEmailSignature(),
                settings.getDashboardLayout()
        );
    }
}
