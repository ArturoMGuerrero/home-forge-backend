-- SQL Server version

ALTER TABLE leads
  ADD listing_type NVARCHAR(20),
  ADD budget_min DECIMAL(14,2),
  ADD country_code NVARCHAR(2),
  ADD state_code NVARCHAR(80),
  ADD city NVARCHAR(120),
  ADD property_type NVARCHAR(40),
  ADD bedrooms_min INTEGER,
  ADD bathrooms_min DECIMAL(4,1),
  ADD financing_type NVARCHAR(40),
  ADD priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  ADD assigned_to NVARCHAR(180),
  ADD next_follow_up_at DATETIME2,
  ADD notes NVARCHAR(MAX);

ALTER TABLE leads
  ADD CONSTRAINT chk_lead_budget_range
  CHECK (budget_min IS NULL OR budget_amount IS NULL OR budget_min <= budget_amount),
  ADD CONSTRAINT chk_lead_bedrooms_min
  CHECK (bedrooms_min IS NULL OR bedrooms_min >= 0),
  ADD CONSTRAINT chk_lead_bathrooms_min
  CHECK (bathrooms_min IS NULL OR bathrooms_min >= 0);

CREATE TABLE lead_activities (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER NOT NULL REFERENCES leads(id),
  activity_type NVARCHAR(40) NOT NULL,
  notes NVARCHAR(MAX) NOT NULL,
  occurred_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  next_follow_up_at DATETIME2,
  created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_lead_activities_lead_occurred
  ON lead_activities(lead_id, occurred_at DESC);

CREATE INDEX idx_leads_next_follow_up
  ON leads(company_id, next_follow_up_at);
