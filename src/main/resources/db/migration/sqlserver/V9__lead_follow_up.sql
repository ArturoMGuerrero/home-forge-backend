-- SQL Server version

-- Add lead follow-up columns
ALTER TABLE leads ADD listing_type NVARCHAR(20);
ALTER TABLE leads ADD budget_min DECIMAL(14,2);
ALTER TABLE leads ADD country_code NVARCHAR(2);
ALTER TABLE leads ADD state_code NVARCHAR(80);
ALTER TABLE leads ADD city NVARCHAR(120);
ALTER TABLE leads ADD property_type NVARCHAR(40);
ALTER TABLE leads ADD bedrooms_min INTEGER;
ALTER TABLE leads ADD bathrooms_min DECIMAL(4,1);
ALTER TABLE leads ADD financing_type NVARCHAR(40);
ALTER TABLE leads ADD priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE leads ADD assigned_to NVARCHAR(180);
ALTER TABLE leads ADD next_follow_up_at DATETIME2;
ALTER TABLE leads ADD notes NVARCHAR(MAX);

-- Create lead_activities table
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

-- Create indexes
CREATE INDEX idx_lead_activities_lead_occurred ON lead_activities(lead_id, occurred_at DESC);
CREATE INDEX idx_leads_next_follow_up ON leads(company_id, next_follow_up_at);
