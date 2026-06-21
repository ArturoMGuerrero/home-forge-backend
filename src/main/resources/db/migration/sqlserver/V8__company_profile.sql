-- SQL Server version

ALTER TABLE companies
  ADD public_description NVARCHAR(MAX),
  ADD mission NVARCHAR(MAX),
  ADD vision NVARCHAR(MAX),
  ADD address NVARCHAR(255),
  ADD city NVARCHAR(120),
  ADD postal_code NVARCHAR(20),
  ADD website_url NVARCHAR(500),
  ADD professional_license NVARCHAR(120),
  ADD years_experience INTEGER;

ALTER TABLE companies
  ADD CONSTRAINT chk_company_years_experience
  CHECK (years_experience IS NULL OR years_experience >= 0);
