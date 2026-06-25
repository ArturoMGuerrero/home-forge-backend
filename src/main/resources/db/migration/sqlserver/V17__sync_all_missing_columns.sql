-- Add missing columns to leads table (only if they don't exist - V9 already added most of these)
-- These columns were added in V9__lead_follow_up.sql, so we skip them to avoid conflicts

-- Add missing columns to properties table
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='listing_type')
    ALTER TABLE properties ADD listing_type NVARCHAR(50);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='property_type')
    ALTER TABLE properties ADD property_type NVARCHAR(50);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='city')
    ALTER TABLE properties ADD city NVARCHAR(120);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='state_code')
    ALTER TABLE properties ADD state_code NVARCHAR(10);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='country_code')
    ALTER TABLE properties ADD country_code CHAR(2);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='postal_code')
    ALTER TABLE properties ADD postal_code NVARCHAR(20);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='latitude')
    ALTER TABLE properties ADD latitude DECIMAL(10,8);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='longitude')
    ALTER TABLE properties ADD longitude DECIMAL(11,8);
