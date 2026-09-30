-- Completa en PostgreSQL las funcionalidades que anteriormente solo tenían
-- migraciones para SQL Server.

CREATE TABLE permissions (
  code VARCHAR(60) PRIMARY KEY, name_en VARCHAR(120) NOT NULL,
  name_es VARCHAR(120) NOT NULL, category VARCHAR(40) NOT NULL,
  description_en VARCHAR(255), description_es VARCHAR(255)
);

CREATE TABLE roles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  code VARCHAR(60) NOT NULL, name VARCHAR(120) NOT NULL, description VARCHAR(255),
  is_system_role BOOLEAN NOT NULL DEFAULT false, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ,
  UNIQUE(company_id, code)
);
CREATE TABLE role_permissions (
  role_id UUID NOT NULL REFERENCES roles(id), permission_code VARCHAR(60) NOT NULL REFERENCES permissions(code),
  PRIMARY KEY(role_id, permission_code)
);
CREATE TABLE user_permissions (
  user_id UUID NOT NULL REFERENCES users(id), permission_code VARCHAR(60) NOT NULL REFERENCES permissions(code),
  granted BOOLEAN NOT NULL DEFAULT true, PRIMARY KEY(user_id, permission_code)
);
ALTER TABLE users ADD COLUMN role_id UUID REFERENCES roles(id);

INSERT INTO permissions(code, name_en, name_es, category) VALUES
('LEAD_VIEW','View Leads','Ver Leads','LEADS'), ('LEAD_CREATE','Create Leads','Crear Leads','LEADS'),
('LEAD_EDIT','Edit Leads','Editar Leads','LEADS'), ('LEAD_DELETE','Delete Leads','Eliminar Leads','LEADS'),
('LEAD_ASSIGN','Assign Leads','Asignar Leads','LEADS'), ('LEAD_EXPORT','Export Leads','Exportar Leads','LEADS'),
('PROPERTY_VIEW','View Properties','Ver Propiedades','PROPERTIES'), ('PROPERTY_CREATE','Create Properties','Crear Propiedades','PROPERTIES'),
('PROPERTY_EDIT','Edit Properties','Editar Propiedades','PROPERTIES'), ('PROPERTY_DELETE','Delete Properties','Eliminar Propiedades','PROPERTIES'),
('PROPERTY_PUBLISH','Publish Properties','Publicar Propiedades','PROPERTIES'), ('DOCUMENT_VIEW','View Documents','Ver Documentos','DOCUMENTS'),
('DOCUMENT_UPLOAD','Upload Documents','Subir Documentos','DOCUMENTS'), ('DOCUMENT_DELETE','Delete Documents','Eliminar Documentos','DOCUMENTS'),
('AGENDA_VIEW','View Agenda','Ver Agenda','AGENDA'), ('AGENDA_CREATE','Create Appointments','Crear Citas','AGENDA'),
('AGENDA_EDIT','Edit Appointments','Editar Citas','AGENDA'), ('AGENDA_DELETE','Delete Appointments','Eliminar Citas','AGENDA'),
('REPORT_VIEW','View Reports','Ver Reportes','REPORTS'), ('REPORT_EXPORT','Export Reports','Exportar Reportes','REPORTS'),
('USER_VIEW','View Users','Ver Usuarios','USERS'), ('USER_CREATE','Create Users','Crear Usuarios','USERS'),
('USER_EDIT','Edit Users','Editar Usuarios','USERS'), ('USER_DELETE','Delete Users','Eliminar Usuarios','USERS'),
('USER_MANAGE_ROLES','Manage User Roles','Gestionar Roles','USERS'), ('TEAM_MANAGE','Manage Teams','Gestionar Equipos','USERS'),
('SETTINGS_COMPANY','Manage Company Settings','Gestionar Configuración','SETTINGS'),
('SETTINGS_SUBSCRIPTION','Manage Subscription','Gestionar Suscripción','SETTINGS'),
('SETTINGS_INTEGRATIONS','Manage Integrations','Gestionar Integraciones','SETTINGS');

CREATE TABLE teams (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  name VARCHAR(120) NOT NULL, description VARCHAR(255), leader_id UUID REFERENCES users(id),
  is_active BOOLEAN NOT NULL DEFAULT true, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE team_members (
  team_id UUID NOT NULL REFERENCES teams(id), user_id UUID NOT NULL REFERENCES users(id),
  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(team_id, user_id)
);
ALTER TABLE leads ADD COLUMN assigned_team_id UUID REFERENCES teams(id), ADD COLUMN assigned_user_id UUID REFERENCES users(id);
ALTER TABLE properties ADD COLUMN assigned_team_id UUID REFERENCES teams(id), ADD COLUMN assigned_user_id UUID REFERENCES users(id);

CREATE TABLE user_activity (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  user_id UUID NOT NULL REFERENCES users(id), activity_type VARCHAR(60) NOT NULL,
  activity_category VARCHAR(40) NOT NULL, entity_type VARCHAR(40), entity_id UUID,
  description_en VARCHAR(255), description_es VARCHAR(255), ip_address VARCHAR(45),
  user_agent VARCHAR(255), metadata TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE user_settings (
  user_id UUID PRIMARY KEY REFERENCES users(id), language VARCHAR(5) NOT NULL DEFAULT 'es',
  timezone VARCHAR(80) NOT NULL DEFAULT 'America/Mexico_City', currency VARCHAR(3) NOT NULL DEFAULT 'MXN',
  email_notifications BOOLEAN NOT NULL DEFAULT true, push_notifications BOOLEAN NOT NULL DEFAULT true,
  notification_new_lead BOOLEAN NOT NULL DEFAULT true, notification_lead_update BOOLEAN NOT NULL DEFAULT true,
  notification_appointment BOOLEAN NOT NULL DEFAULT true, notification_team_activity BOOLEAN NOT NULL DEFAULT false,
  theme VARCHAR(20) NOT NULL DEFAULT 'light', email_signature TEXT,
  dashboard_layout VARCHAR(20) NOT NULL DEFAULT 'default', created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE leads ADD COLUMN score INTEGER NOT NULL DEFAULT 0, ADD COLUMN score_updated_at TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN last_assigned_lead_at TIMESTAMPTZ;
ALTER TABLE lead_activities ADD COLUMN user_id UUID, ADD COLUMN duration_minutes INTEGER,
  ADD COLUMN outcome VARCHAR(50), ADD COLUMN property_id UUID, ADD COLUMN attachments TEXT, ADD COLUMN metadata TEXT;

CREATE TABLE follow_up_tasks (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL, lead_id UUID NOT NULL REFERENCES leads(id),
  title VARCHAR(255) NOT NULL, description TEXT, task_type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING', scheduled_for TIMESTAMPTZ NOT NULL,
  completed_at TIMESTAMPTZ, assigned_to_user_id UUID, priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  reminder_sent_at VARCHAR(255), created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE lead_score_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL, lead_id UUID NOT NULL REFERENCES leads(id),
  old_score INTEGER NOT NULL, new_score INTEGER NOT NULL, reason VARCHAR(255), created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE lead_assignment_rules (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL, name VARCHAR(255) NOT NULL,
  description TEXT, active BOOLEAN NOT NULL DEFAULT true, priority INTEGER NOT NULL DEFAULT 0,
  assignment_strategy VARCHAR(50) NOT NULL DEFAULT 'ROUND_ROBIN', criteria_source VARCHAR(100),
  criteria_listing_type VARCHAR(50), criteria_city VARCHAR(120), criteria_budget_min NUMERIC(15,2),
  criteria_budget_max NUMERIC(15,2), criteria_property_type VARCHAR(50), assigned_user_ids TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);

CREATE TABLE document_templates (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL, name VARCHAR(255) NOT NULL,
  description TEXT, document_type VARCHAR(50) NOT NULL, category VARCHAR(50), content TEXT NOT NULL,
  variables TEXT, is_default BOOLEAN NOT NULL DEFAULT false, active BOOLEAN NOT NULL DEFAULT true,
  version INTEGER NOT NULL DEFAULT 1, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
ALTER TABLE documents ADD COLUMN template_id UUID REFERENCES document_templates(id), ADD COLUMN name VARCHAR(255),
  ADD COLUMN content TEXT, ADD COLUMN file_url VARCHAR(500), ADD COLUMN mime_type VARCHAR(100),
  ADD COLUMN version INTEGER NOT NULL DEFAULT 1, ADD COLUMN created_by_user_id UUID, ADD COLUMN metadata TEXT;
CREATE TABLE document_signatures (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL, document_id UUID NOT NULL REFERENCES documents(id),
  signer_name VARCHAR(255) NOT NULL, signer_email VARCHAR(180) NOT NULL, signer_role VARCHAR(50),
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING', signature_data TEXT, ip_address VARCHAR(45), user_agent VARCHAR(500),
  signed_at TIMESTAMPTZ, sent_at TIMESTAMPTZ, expires_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE message_templates (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  name VARCHAR(255) NOT NULL, description VARCHAR(500), template_type VARCHAR(50) NOT NULL,
  channel VARCHAR(50) NOT NULL, subject VARCHAR(500), content TEXT NOT NULL, variables TEXT,
  category VARCHAR(100), active BOOLEAN NOT NULL DEFAULT true, is_default BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  template_id UUID REFERENCES message_templates(id), notification_type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING', priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  recipient_type VARCHAR(50) NOT NULL, recipient_id UUID, recipient_email VARCHAR(255), recipient_phone VARCHAR(50),
  recipient_name VARCHAR(255), subject VARCHAR(500), content TEXT NOT NULL, html_content TEXT, attachments TEXT,
  metadata TEXT, lead_id UUID REFERENCES leads(id), property_id UUID, task_id UUID, sent_at TIMESTAMPTZ,
  delivered_at TIMESTAMPTZ, read_at TIMESTAMPTZ, failed_at TIMESTAMPTZ, error_message TEXT,
  external_id VARCHAR(255), external_status VARCHAR(100), scheduled_for TIMESTAMPTZ, expires_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE communication_channels (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  channel_type VARCHAR(50) NOT NULL, provider VARCHAR(100) NOT NULL, configuration TEXT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true, verified BOOLEAN NOT NULL DEFAULT false, verified_at TIMESTAMPTZ,
  daily_limit INTEGER, monthly_limit INTEGER, daily_sent INTEGER NOT NULL DEFAULT 0, monthly_sent INTEGER NOT NULL DEFAULT 0,
  last_reset_daily TIMESTAMPTZ, last_reset_monthly TIMESTAMPTZ, metadata TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE push_subscriptions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id), user_id UUID NOT NULL REFERENCES users(id),
  endpoint VARCHAR(500) NOT NULL, p256dh_key VARCHAR(500) NOT NULL, auth_key VARCHAR(500) NOT NULL,
  device_type VARCHAR(50), device_name VARCHAR(255), user_agent VARCHAR(500), active BOOLEAN NOT NULL DEFAULT true,
  last_used_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE notification_preferences (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  user_id UUID NOT NULL UNIQUE REFERENCES users(id), email_enabled BOOLEAN NOT NULL DEFAULT true,
  whatsapp_enabled BOOLEAN NOT NULL DEFAULT false, push_enabled BOOLEAN NOT NULL DEFAULT true, sms_enabled BOOLEAN NOT NULL DEFAULT false,
  lead_notifications BOOLEAN NOT NULL DEFAULT true, task_notifications BOOLEAN NOT NULL DEFAULT true,
  appointment_notifications BOOLEAN NOT NULL DEFAULT true, contract_notifications BOOLEAN NOT NULL DEFAULT true,
  payment_notifications BOOLEAN NOT NULL DEFAULT true, system_notifications BOOLEAN NOT NULL DEFAULT true,
  quiet_hours_start TIME, quiet_hours_end TIME, quiet_days VARCHAR(100), daily_summary BOOLEAN NOT NULL DEFAULT false,
  weekly_summary BOOLEAN NOT NULL DEFAULT false, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

DROP TABLE appointments;
CREATE TABLE appointments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id), title VARCHAR(255) NOT NULL,
  description TEXT, appointment_type VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
  start_time TIMESTAMPTZ NOT NULL, end_time TIMESTAMPTZ NOT NULL, timezone VARCHAR(50) DEFAULT 'America/Mexico_City',
  all_day BOOLEAN NOT NULL DEFAULT false, assigned_user_id UUID REFERENCES users(id), lead_id UUID REFERENCES leads(id),
  property_id UUID, location_type VARCHAR(50), location_address VARCHAR(500), virtual_meeting_url VARCHAR(500),
  reminder_minutes INTEGER, reminder_sent BOOLEAN NOT NULL DEFAULT false, reminder_sent_at TIMESTAMPTZ,
  google_calendar_event_id VARCHAR(255), google_calendar_sync_enabled BOOLEAN NOT NULL DEFAULT false, last_synced_at TIMESTAMPTZ,
  notes TEXT, outcome VARCHAR(50), follow_up_required BOOLEAN NOT NULL DEFAULT false, metadata TEXT, created_by_user_id UUID,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE agent_availability (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id), user_id UUID NOT NULL REFERENCES users(id),
  day_of_week INTEGER NOT NULL CHECK(day_of_week BETWEEN 0 AND 6), start_time TIME NOT NULL, end_time TIME NOT NULL,
  is_available BOOLEAN NOT NULL DEFAULT true, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE calendar_blocks (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id), user_id UUID NOT NULL REFERENCES users(id),
  title VARCHAR(255) NOT NULL, reason VARCHAR(50), start_time TIMESTAMPTZ NOT NULL, end_time TIMESTAMPTZ NOT NULL,
  all_day BOOLEAN NOT NULL DEFAULT false, notes TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE TABLE appointment_reminders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), company_id UUID NOT NULL REFERENCES companies(id),
  appointment_id UUID NOT NULL REFERENCES appointments(id), reminder_type VARCHAR(50) NOT NULL,
  recipient_type VARCHAR(50) NOT NULL, minutes_before INTEGER NOT NULL, scheduled_time TIMESTAMPTZ NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING', sent_at TIMESTAMPTZ, notification_id UUID,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE properties ADD COLUMN owner_name VARCHAR(150), ADD COLUMN owner_email VARCHAR(100),
  ADD COLUMN owner_phone VARCHAR(20), ADD COLUMN owner_phone_secondary VARCHAR(20), ADD COLUMN owner_notes TEXT;

CREATE INDEX idx_follow_up_tasks_scheduled ON follow_up_tasks(scheduled_for);
CREATE INDEX idx_leads_score ON leads(score);
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_for) WHERE deleted_at IS NULL AND status = 'PENDING';
CREATE INDEX idx_appointments_date_range ON appointments(company_id, start_time, end_time) WHERE deleted_at IS NULL;
CREATE INDEX idx_properties_owner_email ON properties(owner_email) WHERE owner_email IS NOT NULL;
CREATE INDEX idx_properties_owner_phone ON properties(owner_phone) WHERE owner_phone IS NOT NULL;
