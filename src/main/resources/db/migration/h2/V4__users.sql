CREATE TABLE users (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  company_id UUID NOT NULL REFERENCES companies(id),
  full_name VARCHAR(180) NOT NULL,
  email VARCHAR(180) NOT NULL,
  phone_e164 VARCHAR(20),
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(40) NOT NULL DEFAULT 'ADMIN',
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP WITH TIME ZONE,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_user_email_lowercase CHECK (email = LOWER(email)),
  CONSTRAINT chk_user_phone CHECK (phone_e164 IS NULL OR phone_e164 REGEXP '^\+[1-9][0-9]{1,14}$')
);

CREATE INDEX idx_users_company_id ON users(company_id);
