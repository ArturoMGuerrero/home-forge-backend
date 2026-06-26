-- Create follow_up_tasks table for automated task scheduling
CREATE TABLE follow_up_tasks (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    lead_id UNIQUEIDENTIFIER NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    task_type NVARCHAR(50) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    scheduled_for DATETIME2 NOT NULL,
    completed_at DATETIME2,
    assigned_to_user_id UNIQUEIDENTIFIER,
    priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    reminder_sent_at NVARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted_at DATETIME2,
    CONSTRAINT fk_follow_up_tasks_lead FOREIGN KEY (lead_id) REFERENCES leads(id)
);

-- Indexes for follow_up_tasks
CREATE INDEX idx_follow_up_tasks_company ON follow_up_tasks(company_id);
CREATE INDEX idx_follow_up_tasks_lead ON follow_up_tasks(lead_id);
CREATE INDEX idx_follow_up_tasks_scheduled ON follow_up_tasks(scheduled_for);
CREATE INDEX idx_follow_up_tasks_status ON follow_up_tasks(status);
CREATE INDEX idx_follow_up_tasks_assigned_user ON follow_up_tasks(assigned_to_user_id);
