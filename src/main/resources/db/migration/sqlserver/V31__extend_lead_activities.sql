-- Add additional columns to lead_activities for richer interaction tracking
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='user_id')
    ALTER TABLE lead_activities ADD user_id UNIQUEIDENTIFIER;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='duration_minutes')
    ALTER TABLE lead_activities ADD duration_minutes INT;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='outcome')
    ALTER TABLE lead_activities ADD outcome NVARCHAR(50);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='property_id')
    ALTER TABLE lead_activities ADD property_id UNIQUEIDENTIFIER;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='attachments')
    ALTER TABLE lead_activities ADD attachments NVARCHAR(MAX);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='metadata')
    ALTER TABLE lead_activities ADD metadata NVARCHAR(MAX);

-- Index on user_id for filtering activities by user
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_lead_activities_user')
    CREATE INDEX idx_lead_activities_user ON lead_activities(user_id);

-- Index on property_id for linking activities to properties
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_lead_activities_property')
    CREATE INDEX idx_lead_activities_property ON lead_activities(property_id);
