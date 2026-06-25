-- SQL Server version
-- Add constraint for years_experience

ALTER TABLE companies ADD CONSTRAINT chk_company_years_experience CHECK (years_experience IS NULL OR years_experience >= 0);
