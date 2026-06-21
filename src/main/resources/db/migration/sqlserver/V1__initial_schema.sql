-- SQL Server version



CREATE TABLE companies (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  name NVARCHAR(180) NOT NULL,
  country_code CHAR(2) NOT NULL,
  state_code NVARCHAR(10) NOT NULL,
  default_language NVARCHAR(5) NOT NULL DEFAULT 'en',
  default_currency CHAR(3) NOT NULL,
  timezone NVARCHAR(80) NOT NULL,
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE subscription_plans (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  code NVARCHAR(60) NOT NULL,
  country_code CHAR(2) NOT NULL,
  name_en NVARCHAR(120) NOT NULL,
  name_es NVARCHAR(120) NOT NULL,
  monthly_price DECIMAL(12,2) NOT NULL,
  annual_price DECIMAL(12,2),
  currency_code CHAR(3) NOT NULL,
  active BIT NOT NULL DEFAULT 1,
  UNIQUE(code, country_code)
);

CREATE TABLE plan_features (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  plan_id UNIQUEIDENTIFIER NOT NULL REFERENCES subscription_plans(id),
  feature_code NVARCHAR(80) NOT NULL,
  feature_value NVARCHAR(120) NOT NULL,
  UNIQUE(plan_id, feature_code)
);

CREATE TABLE leads (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  first_name NVARCHAR(100) NOT NULL,
  middle_name NVARCHAR(100),
  last_name NVARCHAR(100) NOT NULL,
  second_last_name NVARCHAR(100),
  email NVARCHAR(180),
  phone_e164 NVARCHAR(20),
  source NVARCHAR(60),
  status NVARCHAR(40) NOT NULL DEFAULT 'NEW',
  budget_amount DECIMAL(14,2),
  currency_code CHAR(3),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2,
  CONSTRAINT chk_lead_phone CHECK (phone_e164 IS NULL OR phone_e164 LIKE '+[1-9]%')
);

CREATE TABLE developments (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  name NVARCHAR(180) NOT NULL,
  city NVARCHAR(120) NOT NULL,
  state_code NVARCHAR(10) NOT NULL,
  country_code CHAR(2) NOT NULL,
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE properties (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  development_id UNIQUEIDENTIFIER REFERENCES developments(id),
  code NVARCHAR(80) NOT NULL,
  property_type NVARCHAR(40) NOT NULL,
  status NVARCHAR(40) NOT NULL DEFAULT 'AVAILABLE',
  price DECIMAL(14,2) NOT NULL,
  currency_code CHAR(3) NOT NULL,
  bedrooms INT,
  bathrooms DECIMAL(4,1),
  land_area DECIMAL(10,2),
  construction_area DECIMAL(10,2),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2,
  UNIQUE(company_id, code)
);

CREATE TABLE tasks (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER REFERENCES leads(id),
  title NVARCHAR(180) NOT NULL,
  status NVARCHAR(40) NOT NULL DEFAULT 'OPEN',
  due_at DATETIME2,
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE documents (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER REFERENCES leads(id),
  document_type NVARCHAR(80) NOT NULL,
  file_name NVARCHAR(255) NOT NULL,
  status NVARCHAR(40) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE mortgage_applications (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  lead_id UNIQUEIDENTIFIER NOT NULL REFERENCES leads(id),
  mortgage_type NVARCHAR(60) NOT NULL,
  status NVARCHAR(60) NOT NULL DEFAULT 'NOT_STARTED',
  lender_name NVARCHAR(180),
  requested_amount DECIMAL(14,2),
  currency_code CHAR(3),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE ai_conversations (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  company_id UNIQUEIDENTIFIER NOT NULL REFERENCES companies(id),
  title NVARCHAR(180),
  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
  deleted_at DATETIME2
);

CREATE TABLE ai_messages (
  id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
  conversation_id UNIQUEIDENTIFIER NOT NULL REFERENCES ai_conversations(id),
  role NVARCHAR(30) NOT NULL,
  content NVARCHAR(MAX) NOT NULL,
  created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

INSERT INTO subscription_plans(id, code,country_code,name_en,name_es,monthly_price,annual_price,currency_code) VALUES
(NEWID(), 'STARTER','MX','Starter MX','Inicial MX',299,2990,'MXN'),
(NEWID(), 'PRO','MX','Pro MX','Pro MX',999,9990,'MXN'),
(NEWID(), 'BUSINESS','MX','Business MX','Empresarial MX',3999,39990,'MXN'),
(NEWID(), 'STARTER','US','Starter US','Inicial US',19,190,'USD'),
(NEWID(), 'PRO','US','Pro US','Pro US',79,790,'USD'),
(NEWID(), 'BUSINESS','US','Business US','Empresarial US',249,2490,'USD');
