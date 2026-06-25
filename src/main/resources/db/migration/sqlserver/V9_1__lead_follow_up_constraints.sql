-- SQL Server version
-- Add constraints for lead follow-up columns

ALTER TABLE leads ADD CONSTRAINT chk_lead_budget_range CHECK (budget_min IS NULL OR budget_amount IS NULL OR budget_min <= budget_amount);
ALTER TABLE leads ADD CONSTRAINT chk_lead_bedrooms_min CHECK (bedrooms_min IS NULL OR bedrooms_min >= 0);
ALTER TABLE leads ADD CONSTRAINT chk_lead_bathrooms_min CHECK (bathrooms_min IS NULL OR bathrooms_min >= 0);
