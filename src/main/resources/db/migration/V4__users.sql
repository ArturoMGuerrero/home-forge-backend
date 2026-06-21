CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  full_name VARCHAR(180) NOT NULL,
  email VARCHAR(180) NOT NULL,
  phone_e164 VARCHAR(20),
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(40) NOT NULL DEFAULT 'ADMIN',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_user_email_lowercase CHECK (email = lower(email)),
  CONSTRAINT chk_user_phone CHECK (phone_e164 IS NULL OR phone_e164 ~ '^\+[1-9][0-9]{1,14}$')
);

CREATE INDEX idx_users_company_id ON users(company_id);
