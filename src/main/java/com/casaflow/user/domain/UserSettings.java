package com.casaflow.user.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 5)
    private String language = "es";

    @Column(nullable = false, length = 80)
    private String timezone = "America/Mexico_City";

    @Column(nullable = false, length = 3)
    private String currency = "MXN";

    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;

    @Column(name = "push_notifications", nullable = false)
    private boolean pushNotifications = true;

    @Column(name = "notification_new_lead", nullable = false)
    private boolean notificationNewLead = true;

    @Column(name = "notification_lead_update", nullable = false)
    private boolean notificationLeadUpdate = true;

    @Column(name = "notification_appointment", nullable = false)
    private boolean notificationAppointment = true;

    @Column(name = "notification_team_activity", nullable = false)
    private boolean notificationTeamActivity = false;

    @Column(nullable = false, length = 20)
    private String theme = "light";

    @Column(name = "email_signature", columnDefinition = "TEXT")
    private String emailSignature;

    @Column(name = "dashboard_layout", nullable = false, length = 20)
    private String dashboardLayout = "default";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected UserSettings() {
    }

    public UserSettings(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        this.updatedAt = Instant.now();
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        this.updatedAt = Instant.now();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        this.updatedAt = Instant.now();
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
        this.updatedAt = Instant.now();
    }

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
        this.updatedAt = Instant.now();
    }

    public boolean isNotificationNewLead() {
        return notificationNewLead;
    }

    public void setNotificationNewLead(boolean notificationNewLead) {
        this.notificationNewLead = notificationNewLead;
        this.updatedAt = Instant.now();
    }

    public boolean isNotificationLeadUpdate() {
        return notificationLeadUpdate;
    }

    public void setNotificationLeadUpdate(boolean notificationLeadUpdate) {
        this.notificationLeadUpdate = notificationLeadUpdate;
        this.updatedAt = Instant.now();
    }

    public boolean isNotificationAppointment() {
        return notificationAppointment;
    }

    public void setNotificationAppointment(boolean notificationAppointment) {
        this.notificationAppointment = notificationAppointment;
        this.updatedAt = Instant.now();
    }

    public boolean isNotificationTeamActivity() {
        return notificationTeamActivity;
    }

    public void setNotificationTeamActivity(boolean notificationTeamActivity) {
        this.notificationTeamActivity = notificationTeamActivity;
        this.updatedAt = Instant.now();
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        this.updatedAt = Instant.now();
    }

    public String getEmailSignature() {
        return emailSignature;
    }

    public void setEmailSignature(String emailSignature) {
        this.emailSignature = emailSignature;
        this.updatedAt = Instant.now();
    }

    public String getDashboardLayout() {
        return dashboardLayout;
    }

    public void setDashboardLayout(String dashboardLayout) {
        this.dashboardLayout = dashboardLayout;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
