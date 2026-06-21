-- SQL Server version

CREATE TABLE users (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  full_name NVARCHAR(180) NOT NULL,
  email NVARCHAR(180) NOT NULL,
  phone_e164 NVARCHAR(20),
  password_hash NVARCHAR(100) NOT NULL,
  role NVARCHAR(40) NOT NULL DEFAULT 'ADMIN',
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_user_email_lowercase CHECK (email = lower(email)),
  CONSTRAINT chk_user_phone CHECK (phone_e164 IS NULL OR phone_e164 LIKE '+[1-9]%')
);

CREATE INDEX idx_users_company_id ON users(company_id);
