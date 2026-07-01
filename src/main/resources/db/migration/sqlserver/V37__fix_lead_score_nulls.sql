-- V36: Fix NULL values in lead score column
-- Update any existing NULL scores to 0

-- First check if score column exists
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='leads' AND COLUMN_NAME='score')
BEGIN
    -- Drop index if it exists
    IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_leads_score' AND object_id = OBJECT_ID('leads'))
    BEGIN
        DROP INDEX idx_leads_score ON leads;
    END

    -- Update NULL scores to 0
    UPDATE leads
    SET score = 0
    WHERE score IS NULL;

    -- Make column NOT NULL if it isn't already
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='leads' AND COLUMN_NAME='score' AND IS_NULLABLE='YES')
    BEGIN
        ALTER TABLE leads ALTER COLUMN score INT NOT NULL;
    END

    -- Recreate the index
    CREATE INDEX idx_leads_score ON leads(score);
END
