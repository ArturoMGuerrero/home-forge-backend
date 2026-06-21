CREATE TABLE appointments (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID REFERENCES leads(id),
  property_id UUID REFERENCES properties(id),
  title VARCHAR(180) NOT NULL,
  appointment_type VARCHAR(40) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  starts_at TIMESTAMP WITH TIME ZONE NOT NULL,
  ends_at TIMESTAMP WITH TIME ZONE,
  location VARCHAR(255),
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_appointments_company_starts
  ON appointments(company_id, starts_at);

CREATE TABLE lead_property_matches (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID NOT NULL REFERENCES leads(id),
  property_id UUID NOT NULL REFERENCES properties(id),
  status VARCHAR(30) NOT NULL DEFAULT 'SUGGESTED',
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP WITH TIME ZONE,
  UNIQUE(company_id, lead_id, property_id)
);

CREATE INDEX idx_matches_company_lead
  ON lead_property_matches(company_id, lead_id);

ALTER TABLE documents
  ADD COLUMN property_id UUID REFERENCES properties(id);
ALTER TABLE documents
  ADD COLUMN file_path VARCHAR(1000);
ALTER TABLE documents
  ADD COLUMN content_type VARCHAR(180);
ALTER TABLE documents
  ADD COLUMN file_size BIGINT;
ALTER TABLE documents
  ADD COLUMN notes TEXT;
ALTER TABLE documents
  ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_documents_company_created
  ON documents(company_id, created_at DESC);
