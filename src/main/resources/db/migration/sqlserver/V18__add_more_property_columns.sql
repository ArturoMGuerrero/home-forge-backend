-- Add missing columns to properties table
ALTER TABLE properties ADD address NVARCHAR(255);
ALTER TABLE properties ADD title NVARCHAR(255);
ALTER TABLE properties ADD description NVARCHAR(MAX);
ALTER TABLE properties ADD image_url NVARCHAR(500);
ALTER TABLE properties ADD published BIT DEFAULT 0;
ALTER TABLE properties ADD parking_spaces INT;
