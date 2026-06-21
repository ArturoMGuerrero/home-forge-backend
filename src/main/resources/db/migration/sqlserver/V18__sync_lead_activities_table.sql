-- Add missing columns to lead_activities table
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='company_id')
    ALTER TABLE lead_activities ADD company_id UNIQUEIDENTIFIER;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='occurred_at')
    ALTER TABLE lead_activities ADD occurred_at DATETIME2 DEFAULT GETDATE();

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='lead_activities' AND COLUMN_NAME='next_follow_up_at')
    ALTER TABLE lead_activities ADD next_follow_up_at DATETIME2;
