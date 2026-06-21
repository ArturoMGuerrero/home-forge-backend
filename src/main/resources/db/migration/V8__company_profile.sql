ALTER TABLE companies
  ADD COLUMN public_description TEXT,
  ADD COLUMN mission TEXT,
  ADD COLUMN vision TEXT,
  ADD COLUMN address VARCHAR(255),
  ADD COLUMN city VARCHAR(120),
  ADD COLUMN postal_code VARCHAR(20),
  ADD COLUMN website_url VARCHAR(500),
  ADD COLUMN professional_license VARCHAR(120),
  ADD COLUMN years_experience INTEGER;

ALTER TABLE companies
  ADD CONSTRAINT chk_company_years_experience
  CHECK (years_experience IS NULL OR years_experience >= 0);
