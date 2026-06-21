ALTER TABLE companies
  ADD COLUMN public_description TEXT;
ALTER TABLE companies
  ADD COLUMN mission TEXT;
ALTER TABLE companies
  ADD COLUMN vision TEXT;
ALTER TABLE companies
  ADD COLUMN address VARCHAR(255);
ALTER TABLE companies
  ADD COLUMN city VARCHAR(120);
ALTER TABLE companies
  ADD COLUMN postal_code VARCHAR(20);
ALTER TABLE companies
  ADD COLUMN website_url VARCHAR(500);
ALTER TABLE companies
  ADD COLUMN professional_license VARCHAR(120);
ALTER TABLE companies
  ADD COLUMN years_experience INTEGER;

ALTER TABLE companies
  ADD CONSTRAINT chk_company_years_experience
  CHECK (years_experience IS NULL OR years_experience >= 0);
