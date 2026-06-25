-- User settings table
CREATE TABLE user_settings (
  user_id UNIQUEIDENTIFIER PRIMARY KEY REFERENCES users(id),
  language VARCHAR(5) NOT NULL DEFAULT 'es',
  timezone VARCHAR(80) NOT NULL DEFAULT 'America/Mexico_City',
  currency CHAR(3) NOT NULL DEFAULT 'MXN',
  email_notifications BIT NOT NULL DEFAULT 1,
  push_notifications BIT NOT NULL DEFAULT 1,
  notification_new_lead BIT NOT NULL DEFAULT 1,
  notification_lead_update BIT NOT NULL DEFAULT 1,
  notification_appointment BIT NOT NULL DEFAULT 1,
  notification_team_activity BIT NOT NULL DEFAULT 0,
  theme VARCHAR(20) NOT NULL DEFAULT 'light',
  email_signature NVARCHAR(MAX),
  dashboard_layout VARCHAR(20) NOT NULL DEFAULT 'default',
  created_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET(),
  updated_at DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET()
);
