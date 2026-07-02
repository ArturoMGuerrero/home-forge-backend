-- V38: Sistema de Agenda y Citas
-- Calendario interactivo, recordatorios, disponibilidad de agentes

-- Drop old appointments table if exists (from V12)
IF OBJECT_ID('dbo.appointments', 'U') IS NOT NULL
    DROP TABLE appointments;
GO

-- Tabla de citas/appointments
CREATE TABLE appointments (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,

    -- Información básica
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    appointment_type NVARCHAR(50) NOT NULL, -- PROPERTY_TOUR, MEETING, CALL, VIDEO_CALL, SIGNING, OTHER
    status NVARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW, RESCHEDULED

    -- Fechas y horarios
    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NOT NULL,
    timezone NVARCHAR(50) DEFAULT 'America/Mexico_City',
    all_day BIT NOT NULL DEFAULT 0,

    -- Participantes
    assigned_user_id UNIQUEIDENTIFIER, -- Agente asignado
    lead_id UNIQUEIDENTIFIER, -- Prospecto relacionado
    property_id UNIQUEIDENTIFIER, -- Propiedad relacionada

    -- Ubicación
    location_type NVARCHAR(50), -- IN_PERSON, VIRTUAL, PHONE, PROPERTY_SITE
    location_address NVARCHAR(500),
    virtual_meeting_url NVARCHAR(500),

    -- Recordatorios
    reminder_minutes INT, -- Minutos antes para recordatorio (15, 30, 60, etc)
    reminder_sent BIT NOT NULL DEFAULT 0,
    reminder_sent_at DATETIME2,

    -- Sincronización con Google Calendar
    google_calendar_event_id NVARCHAR(255),
    google_calendar_sync_enabled BIT NOT NULL DEFAULT 0,
    last_synced_at DATETIME2,

    -- Notas y seguimiento
    notes NVARCHAR(MAX),
    outcome NVARCHAR(50), -- SUCCESSFUL, RESCHEDULED, NO_SHOW, CANCELLED
    follow_up_required BIT NOT NULL DEFAULT 0,

    -- Metadata
    metadata NVARCHAR(MAX), -- JSON

    -- Auditoría
    created_by_user_id UNIQUEIDENTIFIER,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2,

    CONSTRAINT FK_appointments_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_appointments_user FOREIGN KEY (assigned_user_id) REFERENCES users(id),
    CONSTRAINT FK_appointments_lead FOREIGN KEY (lead_id) REFERENCES leads(id)
);

-- Tabla de disponibilidad de agentes
CREATE TABLE agent_availability (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    user_id UNIQUEIDENTIFIER NOT NULL,

    -- Día de la semana (0=Domingo, 6=Sábado)
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),

    -- Horarios
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    -- Estado
    active BIT NOT NULL DEFAULT 1,

    -- Excepciones (días específicos no disponibles)
    exception_date DATE,

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_agent_availability_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_agent_availability_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de bloqueos de calendario (días/horarios no disponibles)
CREATE TABLE calendar_blocks (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    user_id UNIQUEIDENTIFIER NOT NULL,

    title NVARCHAR(255) NOT NULL,
    reason NVARCHAR(50), -- VACATION, SICK_LEAVE, MEETING, PERSONAL, OTHER

    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NOT NULL,
    all_day BIT NOT NULL DEFAULT 0,

    notes NVARCHAR(MAX),

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2,

    CONSTRAINT FK_calendar_blocks_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_calendar_blocks_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de recordatorios de citas
CREATE TABLE appointment_reminders (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    appointment_id UNIQUEIDENTIFIER NOT NULL,

    reminder_type NVARCHAR(50) NOT NULL, -- EMAIL, WHATSAPP, PUSH, SMS
    recipient_type NVARCHAR(50) NOT NULL, -- AGENT, LEAD, BOTH

    minutes_before INT NOT NULL, -- 15, 30, 60, 1440 (24h), etc
    scheduled_time DATETIME2 NOT NULL,

    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    sent_at DATETIME2,

    notification_id UNIQUEIDENTIFIER, -- Referencia a tabla notifications

    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_appointment_reminders_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_appointment_reminders_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- Índices para appointments
CREATE INDEX idx_appointments_company ON appointments(company_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_assigned_user ON appointments(assigned_user_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_lead ON appointments(lead_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_property ON appointments(property_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_start_time ON appointments(start_time) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_status ON appointments(status) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_type ON appointments(appointment_type) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_date_range ON appointments(company_id, start_time, end_time) WHERE (deleted_at IS NULL);
CREATE INDEX idx_appointments_google_event ON appointments(google_calendar_event_id) WHERE (google_calendar_event_id IS NOT NULL);

-- Índices para agent_availability
CREATE INDEX idx_agent_availability_user ON agent_availability(user_id) WHERE (active = 1);
CREATE INDEX idx_agent_availability_day ON agent_availability(day_of_week) WHERE (active = 1);
CREATE INDEX idx_agent_availability_company ON agent_availability(company_id);

-- Índices para calendar_blocks
CREATE INDEX idx_calendar_blocks_user ON calendar_blocks(user_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_calendar_blocks_company ON calendar_blocks(company_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_calendar_blocks_date_range ON calendar_blocks(user_id, start_time, end_time) WHERE (deleted_at IS NULL);

-- Índices para appointment_reminders
CREATE INDEX idx_appointment_reminders_appointment ON appointment_reminders(appointment_id);
CREATE INDEX idx_appointment_reminders_scheduled ON appointment_reminders(scheduled_time, status) WHERE (status = 'PENDING');
CREATE INDEX idx_appointment_reminders_company ON appointment_reminders(company_id);
