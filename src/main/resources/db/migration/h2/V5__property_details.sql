ALTER TABLE properties
  ADD COLUMN listing_type VARCHAR(20) NOT NULL DEFAULT 'SALE';
ALTER TABLE properties
  ADD COLUMN title VARCHAR(180) NOT NULL DEFAULT 'Propiedad sin título';
ALTER TABLE properties
  ADD COLUMN country_code VARCHAR(2) NOT NULL DEFAULT 'MX';
ALTER TABLE properties
  ADD COLUMN state_code VARCHAR(80) NOT NULL DEFAULT 'Sin especificar';
ALTER TABLE properties
  ADD COLUMN city VARCHAR(120) NOT NULL DEFAULT 'Sin especificar';
ALTER TABLE properties
  ADD COLUMN address VARCHAR(255);
ALTER TABLE properties
  ADD COLUMN parking_spaces INT;
ALTER TABLE properties
  ADD COLUMN description TEXT;
ALTER TABLE properties
  ADD COLUMN image_url VARCHAR(1000);
ALTER TABLE properties
  ADD COLUMN published BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE properties
  ADD CONSTRAINT chk_property_listing_type CHECK (listing_type IN ('SALE', 'RENT'));
ALTER TABLE properties
  ADD CONSTRAINT chk_property_bedrooms CHECK (bedrooms IS NULL OR bedrooms >= 0);
ALTER TABLE properties
  ADD CONSTRAINT chk_property_bathrooms CHECK (bathrooms IS NULL OR bathrooms >= 0);
ALTER TABLE properties
  ADD CONSTRAINT chk_property_land_area CHECK (land_area IS NULL OR land_area >= 0);
ALTER TABLE properties
  ADD CONSTRAINT chk_property_construction_area CHECK (construction_area IS NULL OR construction_area >= 0);
ALTER TABLE properties
  ADD CONSTRAINT chk_property_parking_spaces CHECK (parking_spaces IS NULL OR parking_spaces >= 0);
