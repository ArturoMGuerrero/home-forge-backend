-- SQL Server version

ALTER TABLE properties
  ADD listing_type NVARCHAR(20) NOT NULL DEFAULT 'SALE',
  ADD title NVARCHAR(180) NOT NULL DEFAULT 'Propiedad sin título',
  ADD country_code NVARCHAR(2) NOT NULL DEFAULT 'MX',
  ADD state_code NVARCHAR(80) NOT NULL DEFAULT 'Sin especificar',
  ADD city NVARCHAR(120) NOT NULL DEFAULT 'Sin especificar',
  ADD address NVARCHAR(255),
  ADD parking_spaces INT,
  ADD description NVARCHAR(MAX),
  ADD image_url NVARCHAR(1000),
  ADD published BIT NOT NULL DEFAULT 0;

ALTER TABLE properties
  ADD CONSTRAINT chk_property_listing_type CHECK (listing_type IN ('SALE', 'RENT')),
  ADD CONSTRAINT chk_property_bedrooms CHECK (bedrooms IS NULL OR bedrooms >= 0),
  ADD CONSTRAINT chk_property_bathrooms CHECK (bathrooms IS NULL OR bathrooms >= 0),
  ADD CONSTRAINT chk_property_land_area CHECK (land_area IS NULL OR land_area >= 0),
  ADD CONSTRAINT chk_property_construction_area CHECK (construction_area IS NULL OR construction_area >= 0),
  ADD CONSTRAINT chk_property_parking_spaces CHECK (parking_spaces IS NULL OR parking_spaces >= 0);
