-- Los contratos generados desde plantillas compartían la tabla "documents" con los archivos subidos,
-- que exige file_name; por eso nunca se pudo crear uno. Pasan a su propia tabla.
CREATE TABLE IF NOT EXISTS generated_documents (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  template_id UUID REFERENCES document_templates(id),
  name VARCHAR(255) NOT NULL,
  document_type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
  content TEXT,
  file_url VARCHAR(500),
  file_size BIGINT,
  mime_type VARCHAR(100),
  version INTEGER NOT NULL DEFAULT 1,
  lead_id UUID REFERENCES leads(id),
  property_id UUID REFERENCES properties(id),
  created_by_user_id UUID REFERENCES users(id),
  metadata TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_generated_documents_company
  ON generated_documents(company_id, created_at DESC) WHERE deleted_at IS NULL;

-- Las firmas pertenecen a los contratos generados. No podía existir ninguna válida (no había forma
-- de crear contratos), así que se descartan las huérfanas antes de cambiar la referencia.
DO $$
DECLARE fk_name text;
BEGIN
  FOR fk_name IN
    SELECT conname FROM pg_constraint
    WHERE conrelid = 'document_signatures'::regclass AND contype = 'f'
      AND confrelid = 'documents'::regclass
  LOOP
    EXECUTE format('ALTER TABLE document_signatures DROP CONSTRAINT %I', fk_name);
  END LOOP;
END $$;

DELETE FROM document_signatures WHERE document_id NOT IN (SELECT id FROM generated_documents);

ALTER TABLE document_signatures
  ADD CONSTRAINT fk_document_signatures_generated_document
  FOREIGN KEY (document_id) REFERENCES generated_documents(id);
