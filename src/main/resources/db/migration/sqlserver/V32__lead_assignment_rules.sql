-- Create lead assignment rules table
CREATE TABLE lead_assignment_rules (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    active BIT NOT NULL DEFAULT 1,
    priority INT NOT NULL DEFAULT 0,
    assignment_strategy NVARCHAR(50) NOT NULL DEFAULT 'ROUND_ROBIN',
    criteria_source NVARCHAR(100),
    criteria_listing_type NVARCHAR(50),
    criteria_city NVARCHAR(120),
    criteria_budget_min DECIMAL(15,2),
    criteria_budget_max DECIMAL(15,2),
    criteria_property_type NVARCHAR(50),
    assigned_user_ids NVARCHAR(MAX),
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted_at DATETIME2
);

-- Indexes for lead_assignment_rules
CREATE INDEX idx_assignment_rules_company ON lead_assignment_rules(company_id);
CREATE INDEX idx_assignment_rules_active ON lead_assignment_rules(active);
CREATE INDEX idx_assignment_rules_priority ON lead_assignment_rules(priority);

-- Add last_assigned_at column to users for round-robin tracking
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='last_assigned_lead_at')
    ALTER TABLE users ADD last_assigned_lead_at DATETIME2;
