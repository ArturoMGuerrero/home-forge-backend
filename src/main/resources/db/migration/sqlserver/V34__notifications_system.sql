-- V34: Sistema de Notificaciones y Comunicación
-- Tablas para emails, notificaciones push, WhatsApp y templates de mensajes

-- Tabla de templates de mensajes reutilizables
CREATE TABLE message_templates (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(500),
    template_type NVARCHAR(50) NOT NULL, -- EMAIL, SMS, WHATSAPP, PUSH
    channel NVARCHAR(50) NOT NULL, -- EMAIL, WHATSAPP, PUSH, SMS
    subject NVARCHAR(500), -- Para emails
    content NVARCHAR(MAX) NOT NULL, -- Contenido con variables {{variable}}
    variables NVARCHAR(MAX), -- JSON con lista de variables disponibles
    category NVARCHAR(100), -- LEAD_FOLLOWUP, APPOINTMENT, CONTRACT, PAYMENT, GENERAL
    active BIT NOT NULL DEFAULT 1,
    is_default BIT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2,
    CONSTRAINT FK_message_templates_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Tabla de notificaciones (emails, WhatsApp, push)
CREATE TABLE notifications (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    template_id UNIQUEIDENTIFIER,
    notification_type NVARCHAR(50) NOT NULL, -- EMAIL, WHATSAPP, PUSH, SMS
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, DELIVERED, READ, FAILED, CANCELLED
    priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT

    -- Destinatario
    recipient_type NVARCHAR(50) NOT NULL, -- LEAD, USER, CUSTOM
    recipient_id UNIQUEIDENTIFIER, -- lead_id o user_id
    recipient_email NVARCHAR(255),
    recipient_phone NVARCHAR(50),
    recipient_name NVARCHAR(255),

    -- Contenido
    subject NVARCHAR(500),
    content NVARCHAR(MAX) NOT NULL,
    html_content NVARCHAR(MAX), -- Para emails HTML
    attachments NVARCHAR(MAX), -- JSON array de URLs

    -- Metadatos
    metadata NVARCHAR(MAX), -- JSON con datos adicionales
    lead_id UNIQUEIDENTIFIER, -- Relación opcional con lead
    property_id UNIQUEIDENTIFIER, -- Relación opcional con propiedad
    task_id UNIQUEIDENTIFIER, -- Relación opcional con tarea

    -- Tracking
    sent_at DATETIME2,
    delivered_at DATETIME2,
    read_at DATETIME2,
    failed_at DATETIME2,
    error_message NVARCHAR(MAX),

    -- Proveedores externos
    external_id NVARCHAR(255), -- ID del proveedor (SendGrid, Twilio, etc.)
    external_status NVARCHAR(100),

    -- Programación
    scheduled_for DATETIME2,
    expires_at DATETIME2,

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2,

    CONSTRAINT FK_notifications_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_notifications_template FOREIGN KEY (template_id) REFERENCES message_templates(id),
    CONSTRAINT FK_notifications_lead FOREIGN KEY (lead_id) REFERENCES leads(id)
);

-- Tabla de configuración de canales de comunicación
CREATE TABLE communication_channels (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    channel_type NVARCHAR(50) NOT NULL, -- EMAIL, WHATSAPP, SMS, PUSH
    provider NVARCHAR(100) NOT NULL, -- SENDGRID, TWILIO, FIREBASE, WHATSAPP_BUSINESS

    -- Configuración (JSON con API keys, tokens, etc.)
    configuration NVARCHAR(MAX) NOT NULL, -- JSON encriptado

    -- Estado
    active BIT NOT NULL DEFAULT 1,
    verified BIT NOT NULL DEFAULT 0,
    verified_at DATETIME2,

    -- Límites y uso
    daily_limit INT,
    monthly_limit INT,
    daily_sent INT NOT NULL DEFAULT 0,
    monthly_sent INT NOT NULL DEFAULT 0,
    last_reset_daily DATETIME2,
    last_reset_monthly DATETIME2,

    -- Metadatos
    metadata NVARCHAR(MAX),

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2,

    CONSTRAINT FK_communication_channels_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT UQ_communication_channels_company_type UNIQUE (company_id, channel_type, deleted_at)
);

-- Tabla de suscripciones a notificaciones push
CREATE TABLE push_subscriptions (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    user_id UNIQUEIDENTIFIER NOT NULL,

    -- Push subscription data (Web Push API)
    endpoint NVARCHAR(500) NOT NULL,
    p256dh_key NVARCHAR(500) NOT NULL,
    auth_key NVARCHAR(500) NOT NULL,

    -- Device info
    device_type NVARCHAR(50), -- WEB, ANDROID, IOS
    device_name NVARCHAR(255),
    user_agent NVARCHAR(500),

    active BIT NOT NULL DEFAULT 1,
    last_used_at DATETIME2,

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_push_subscriptions_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_push_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de preferencias de notificaciones por usuario
CREATE TABLE notification_preferences (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    user_id UNIQUEIDENTIFIER NOT NULL,

    -- Preferencias por canal
    email_enabled BIT NOT NULL DEFAULT 1,
    whatsapp_enabled BIT NOT NULL DEFAULT 0,
    push_enabled BIT NOT NULL DEFAULT 1,
    sms_enabled BIT NOT NULL DEFAULT 0,

    -- Preferencias por categoría
    lead_notifications BIT NOT NULL DEFAULT 1,
    task_notifications BIT NOT NULL DEFAULT 1,
    appointment_notifications BIT NOT NULL DEFAULT 1,
    contract_notifications BIT NOT NULL DEFAULT 1,
    payment_notifications BIT NOT NULL DEFAULT 1,
    system_notifications BIT NOT NULL DEFAULT 1,

    -- Horarios (no molestar)
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    quiet_days NVARCHAR(100), -- JSON array: ["saturday", "sunday"]

    -- Frecuencia de resúmenes
    daily_summary BIT NOT NULL DEFAULT 0,
    weekly_summary BIT NOT NULL DEFAULT 0,

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_notification_preferences_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT UQ_notification_preferences_user UNIQUE (user_id)
);

-- Índices para notifications
CREATE INDEX idx_notifications_company ON notifications(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_status ON notifications(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_type ON notifications(notification_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_lead ON notifications(lead_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_for) WHERE deleted_at IS NULL AND status = 'PENDING';
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_notifications_created_at ON notifications(created_at) WHERE deleted_at IS NULL;

-- Índices para message_templates
CREATE INDEX idx_message_templates_company ON message_templates(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_message_templates_type ON message_templates(template_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_message_templates_category ON message_templates(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_message_templates_active ON message_templates(active) WHERE deleted_at IS NULL;

-- Índices para communication_channels
CREATE INDEX idx_communication_channels_company ON communication_channels(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_communication_channels_type ON communication_channels(channel_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_communication_channels_active ON communication_channels(active) WHERE deleted_at IS NULL;

-- Índices para push_subscriptions
CREATE INDEX idx_push_subscriptions_user ON push_subscriptions(user_id) WHERE active = 1;
CREATE INDEX idx_push_subscriptions_company ON push_subscriptions(company_id) WHERE active = 1;

-- Índices para notification_preferences
CREATE INDEX idx_notification_preferences_company ON notification_preferences(company_id);
CREATE INDEX idx_notification_preferences_user ON notification_preferences(user_id);
