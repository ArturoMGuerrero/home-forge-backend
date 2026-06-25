-- Permissions enum table (catalog)
CREATE TABLE permissions (
  code VARCHAR(60) PRIMARY KEY,
  name_en VARCHAR(120) NOT NULL,
  name_es VARCHAR(120) NOT NULL,
  category VARCHAR(40) NOT NULL,
  description_en VARCHAR(255),
  description_es VARCHAR(255)
);

-- Custom roles (beyond ADMIN/AGENT)
CREATE TABLE roles (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  code VARCHAR(60) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255),
  is_system_role BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP WITH TIME ZONE,
  UNIQUE(company_id, code)
);

-- Role permissions (many-to-many)
CREATE TABLE role_permissions (
  role_id UUID NOT NULL REFERENCES roles(id),
  permission_code VARCHAR(60) NOT NULL REFERENCES permissions(code),
  PRIMARY KEY(role_id, permission_code)
);

-- User custom permissions (overrides role permissions)
CREATE TABLE user_permissions (
  user_id UUID NOT NULL REFERENCES users(id),
  permission_code VARCHAR(60) NOT NULL REFERENCES permissions(code),
  granted BOOLEAN NOT NULL DEFAULT true,
  PRIMARY KEY(user_id, permission_code)
);

-- Add role_id to users table
ALTER TABLE users ADD COLUMN role_id UUID REFERENCES roles(id);

-- Insert permission catalog
INSERT INTO permissions(code, name_en, name_es, category, description_en, description_es) VALUES
-- Leads
('LEAD_VIEW', 'View Leads', 'Ver Leads', 'LEADS', 'View lead list and details', 'Ver lista y detalles de leads'),
('LEAD_CREATE', 'Create Leads', 'Crear Leads', 'LEADS', 'Create new leads', 'Crear nuevos leads'),
('LEAD_EDIT', 'Edit Leads', 'Editar Leads', 'LEADS', 'Edit lead information', 'Editar información de leads'),
('LEAD_DELETE', 'Delete Leads', 'Eliminar Leads', 'LEADS', 'Delete leads', 'Eliminar leads'),
('LEAD_ASSIGN', 'Assign Leads', 'Asignar Leads', 'LEADS', 'Assign leads to users/teams', 'Asignar leads a usuarios/equipos'),
('LEAD_EXPORT', 'Export Leads', 'Exportar Leads', 'LEADS', 'Export lead data', 'Exportar datos de leads'),

-- Properties
('PROPERTY_VIEW', 'View Properties', 'Ver Propiedades', 'PROPERTIES', 'View property list and details', 'Ver lista y detalles de propiedades'),
('PROPERTY_CREATE', 'Create Properties', 'Crear Propiedades', 'PROPERTIES', 'Create new properties', 'Crear nuevas propiedades'),
('PROPERTY_EDIT', 'Edit Properties', 'Editar Propiedades', 'PROPERTIES', 'Edit property information', 'Editar información de propiedades'),
('PROPERTY_DELETE', 'Delete Properties', 'Eliminar Propiedades', 'PROPERTIES', 'Delete properties', 'Eliminar propiedades'),
('PROPERTY_PUBLISH', 'Publish Properties', 'Publicar Propiedades', 'PROPERTIES', 'Publish properties to public catalog', 'Publicar propiedades al catálogo público'),

-- Documents
('DOCUMENT_VIEW', 'View Documents', 'Ver Documentos', 'DOCUMENTS', 'View document list', 'Ver lista de documentos'),
('DOCUMENT_UPLOAD', 'Upload Documents', 'Subir Documentos', 'DOCUMENTS', 'Upload new documents', 'Subir nuevos documentos'),
('DOCUMENT_DELETE', 'Delete Documents', 'Eliminar Documentos', 'DOCUMENTS', 'Delete documents', 'Eliminar documentos'),

-- Agenda
('AGENDA_VIEW', 'View Agenda', 'Ver Agenda', 'AGENDA', 'View appointments', 'Ver citas'),
('AGENDA_CREATE', 'Create Appointments', 'Crear Citas', 'AGENDA', 'Create new appointments', 'Crear nuevas citas'),
('AGENDA_EDIT', 'Edit Appointments', 'Editar Citas', 'AGENDA', 'Edit appointments', 'Editar citas'),
('AGENDA_DELETE', 'Delete Appointments', 'Eliminar Citas', 'AGENDA', 'Delete appointments', 'Eliminar citas'),

-- Reports
('REPORT_VIEW', 'View Reports', 'Ver Reportes', 'REPORTS', 'View reports and analytics', 'Ver reportes y análisis'),
('REPORT_EXPORT', 'Export Reports', 'Exportar Reportes', 'REPORTS', 'Export report data', 'Exportar datos de reportes'),

-- Users & Teams
('USER_VIEW', 'View Users', 'Ver Usuarios', 'USERS', 'View user list', 'Ver lista de usuarios'),
('USER_CREATE', 'Create Users', 'Crear Usuarios', 'USERS', 'Create new users', 'Crear nuevos usuarios'),
('USER_EDIT', 'Edit Users', 'Editar Usuarios', 'USERS', 'Edit user information', 'Editar información de usuarios'),
('USER_DELETE', 'Delete Users', 'Eliminar Usuarios', 'USERS', 'Delete users', 'Eliminar usuarios'),
('USER_MANAGE_ROLES', 'Manage User Roles', 'Gestionar Roles', 'USERS', 'Assign roles to users', 'Asignar roles a usuarios'),
('TEAM_MANAGE', 'Manage Teams', 'Gestionar Equipos', 'USERS', 'Create and manage teams', 'Crear y gestionar equipos'),

-- Settings
('SETTINGS_COMPANY', 'Manage Company Settings', 'Gestionar Configuración', 'SETTINGS', 'Manage company profile and settings', 'Gestionar perfil y configuración de empresa'),
('SETTINGS_SUBSCRIPTION', 'Manage Subscription', 'Gestionar Suscripción', 'SETTINGS', 'Manage subscription and billing', 'Gestionar suscripción y facturación'),
('SETTINGS_INTEGRATIONS', 'Manage Integrations', 'Gestionar Integraciones', 'SETTINGS', 'Manage third-party integrations', 'Gestionar integraciones de terceros');
