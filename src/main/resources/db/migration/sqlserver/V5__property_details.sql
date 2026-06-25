-- SQL Server version

-- Add columns
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

-- Add constraints
ALTER TABLE properties ADD CONSTRAINT chk_property_listing_type CHECK (listing_type IN ('SALE', 'RENT'));
ALTER TABLE properties ADD CONSTRAINT chk_property_bedrooms CHECK (bedrooms IS NULL OR bedrooms >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_bathrooms CHECK (bathrooms IS NULL OR bathrooms >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_land_area CHECK (land_area IS NULL OR land_area >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_construction_area CHECK (construction_area IS NULL OR construction_area >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_parking_spaces CHECK (parking_spaces IS NULL OR parking_spaces >= 0);
