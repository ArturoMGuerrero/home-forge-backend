-- Add missing columns to properties table (only if they don't exist - V5 already added most of these)
-- These columns were added in V5__property_details.sql, so we use IF NOT EXISTS to avoid conflicts
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='address')
    ALTER TABLE properties ADD address NVARCHAR(255);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='title')
    ALTER TABLE properties ADD title NVARCHAR(255);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='description')
    ALTER TABLE properties ADD description NVARCHAR(MAX);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='image_url')
    ALTER TABLE properties ADD image_url NVARCHAR(500);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='published')
    ALTER TABLE properties ADD published BIT DEFAULT 0;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='parking_spaces')
    ALTER TABLE properties ADD parking_spaces INT;
