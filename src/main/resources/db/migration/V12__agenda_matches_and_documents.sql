CREATE TABLE appointments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID REFERENCES leads(id),
  property_id UUID REFERENCES properties(id),
  title VARCHAR(180) NOT NULL,
  appointment_type VARCHAR(40) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ,
  location VARCHAR(255),
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_appointments_company_starts
  ON appointments(company_id, starts_at);

CREATE TABLE lead_property_matches (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID NOT NULL REFERENCES leads(id),
  property_id UUID NOT NULL REFERENCES properties(id),
  status VARCHAR(30) NOT NULL DEFAULT 'SUGGESTED',
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ,
  UNIQUE(company_id, lead_id, property_id)
);

CREATE INDEX idx_matches_company_lead
  ON lead_property_matches(company_id, lead_id);

ALTER TABLE documents
  ADD COLUMN property_id UUID REFERENCES properties(id),
  ADD COLUMN file_path VARCHAR(1000),
  ADD COLUMN content_type VARCHAR(180),
  ADD COLUMN file_size BIGINT,
  ADD COLUMN notes TEXT,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

CREATE INDEX idx_documents_company_created
  ON documents(company_id, created_at DESC);
