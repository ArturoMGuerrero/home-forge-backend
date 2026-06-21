ALTER TABLE leads
  ADD COLUMN listing_type VARCHAR(20),
  ADD COLUMN budget_min NUMERIC(14,2),
  ADD COLUMN country_code VARCHAR(2),
  ADD COLUMN state_code VARCHAR(80),
  ADD COLUMN city VARCHAR(120),
  ADD COLUMN property_type VARCHAR(40),
  ADD COLUMN bedrooms_min INTEGER,
  ADD COLUMN bathrooms_min NUMERIC(4,1),
  ADD COLUMN financing_type VARCHAR(40),
  ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  ADD COLUMN assigned_to VARCHAR(180),
  ADD COLUMN next_follow_up_at TIMESTAMPTZ,
  ADD COLUMN notes TEXT;

ALTER TABLE leads
  ADD CONSTRAINT chk_lead_budget_range
  CHECK (budget_min IS NULL OR budget_amount IS NULL OR budget_min <= budget_amount),
  ADD CONSTRAINT chk_lead_bedrooms_min
  CHECK (bedrooms_min IS NULL OR bedrooms_min >= 0),
  ADD CONSTRAINT chk_lead_bathrooms_min
  CHECK (bathrooms_min IS NULL OR bathrooms_min >= 0);

CREATE TABLE lead_activities (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID NOT NULL REFERENCES leads(id),
  activity_type VARCHAR(40) NOT NULL,
  notes TEXT NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  next_follow_up_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lead_activities_lead_occurred
  ON lead_activities(lead_id, occurred_at DESC);

CREATE INDEX idx_leads_next_follow_up
  ON leads(company_id, next_follow_up_at);
