-- SQL Server version
-- Add constraints for properties table (separated from column additions)

ALTER TABLE properties ADD CONSTRAINT chk_property_listing_type CHECK (listing_type IN ('SALE', 'RENT'));
ALTER TABLE properties ADD CONSTRAINT chk_property_bedrooms CHECK (bedrooms IS NULL OR bedrooms >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_bathrooms CHECK (bathrooms IS NULL OR bathrooms >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_land_area CHECK (land_area IS NULL OR land_area >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_construction_area CHECK (construction_area IS NULL OR construction_area >= 0);
ALTER TABLE properties ADD CONSTRAINT chk_property_parking_spaces CHECK (parking_spaces IS NULL OR parking_spaces >= 0);
