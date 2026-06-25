-- SQL Server version

-- Add company profile columns
ALTER TABLE companies ADD public_description NVARCHAR(MAX);
ALTER TABLE companies ADD mission NVARCHAR(MAX);
ALTER TABLE companies ADD vision NVARCHAR(MAX);
ALTER TABLE companies ADD address NVARCHAR(255);
ALTER TABLE companies ADD city NVARCHAR(120);
ALTER TABLE companies ADD postal_code NVARCHAR(20);
ALTER TABLE companies ADD website_url NVARCHAR(500);
ALTER TABLE companies ADD professional_license NVARCHAR(120);
ALTER TABLE companies ADD years_experience INTEGER;
