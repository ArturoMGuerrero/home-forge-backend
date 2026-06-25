-- User settings table
CREATE TABLE user_settings (
  user_id UUID PRIMARY KEY REFERENCES users(id),
  language VARCHAR(5) NOT NULL DEFAULT 'es',
  timezone VARCHAR(80) NOT NULL DEFAULT 'America/Mexico_City',
  currency CHAR(3) NOT NULL DEFAULT 'MXN',
  email_notifications BOOLEAN NOT NULL DEFAULT true,
  push_notifications BOOLEAN NOT NULL DEFAULT true,
  notification_new_lead BOOLEAN NOT NULL DEFAULT true,
  notification_lead_update BOOLEAN NOT NULL DEFAULT true,
  notification_appointment BOOLEAN NOT NULL DEFAULT true,
  notification_team_activity BOOLEAN NOT NULL DEFAULT false,
  theme VARCHAR(20) NOT NULL DEFAULT 'light',
  email_signature TEXT,
  dashboard_layout VARCHAR(20) NOT NULL DEFAULT 'default',
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
