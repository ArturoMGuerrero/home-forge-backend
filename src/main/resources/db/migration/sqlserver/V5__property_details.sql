-- SQL Server version

-- Add new columns to properties table
ALTER TABLE properties ADD listing_type NVARCHAR(20) NOT NULL DEFAULT 'SALE';
ALTER TABLE properties ADD title NVARCHAR(180) NOT NULL DEFAULT 'Propiedad sin título';
ALTER TABLE properties ADD country_code NVARCHAR(2) NOT NULL DEFAULT 'MX';
ALTER TABLE properties ADD state_code NVARCHAR(80) NOT NULL DEFAULT 'Sin especificar';
ALTER TABLE properties ADD city NVARCHAR(120) NOT NULL DEFAULT 'Sin especificar';
ALTER TABLE properties ADD address NVARCHAR(255);
ALTER TABLE properties ADD parking_spaces INT;
ALTER TABLE properties ADD description NVARCHAR(MAX);
ALTER TABLE properties ADD image_url NVARCHAR(1000);
ALTER TABLE properties ADD published BIT NOT NULL DEFAULT 0;
