-- SQL Server version

CREATE TABLE appointments (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER REFERENCES leads(id),
  property_id UNIQUEIDENTIFIER REFERENCES properties(id),
  title NVARCHAR(180) NOT NULL,
  appointment_type NVARCHAR(40) NOT NULL,
  status NVARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  starts_at DATETIME2 NOT NULL,
  ends_at DATETIME2,
  location NVARCHAR(255),
  notes NVARCHAR(MAX),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE INDEX idx_appointments_company_starts
  ON appointments(company_id, starts_at);

CREATE TABLE lead_property_matches (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER NOT NULL REFERENCES leads(id),
  property_id UNIQUEIDENTIFIER NOT NULL REFERENCES properties(id),
  status NVARCHAR(30) NOT NULL DEFAULT 'SUGGESTED',
  notes NVARCHAR(MAX),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2,
  UNIQUE(company_id, lead_id, property_id)
);

CREATE INDEX idx_matches_company_lead
  ON lead_property_matches(company_id, lead_id);

ALTER TABLE documents ADD property_id UNIQUEIDENTIFIER REFERENCES properties(id);
ALTER TABLE documents ADD file_path NVARCHAR(1000);
ALTER TABLE documents ADD content_type NVARCHAR(180);
ALTER TABLE documents ADD file_size BIGINT;
ALTER TABLE documents ADD notes NVARCHAR(MAX);
ALTER TABLE documents ADD updated_at DATETIME2 NOT NULL DEFAULT GETDATE();

CREATE INDEX idx_documents_company_created
  ON documents(company_id, created_at DESC);
