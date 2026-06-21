CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE companies (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(180) NOT NULL,
  country_code CHAR(2) NOT NULL,
  state_code VARCHAR(10) NOT NULL,
  default_language VARCHAR(5) NOT NULL DEFAULT 'en',
  default_currency CHAR(3) NOT NULL,
  timezone VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE subscription_plans (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code VARCHAR(60) NOT NULL,
  country_code CHAR(2) NOT NULL,
  name_en VARCHAR(120) NOT NULL,
  name_es VARCHAR(120) NOT NULL,
  monthly_price NUMERIC(12,2) NOT NULL,
  annual_price NUMERIC(12,2),
  currency_code CHAR(3) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true,
  UNIQUE(code, country_code)
);

CREATE TABLE plan_features (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  plan_id UUID NOT NULL REFERENCES subscription_plans(id),
  feature_code VARCHAR(80) NOT NULL,
  feature_value VARCHAR(120) NOT NULL,
  UNIQUE(plan_id, feature_code)
);

CREATE TABLE leads (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  first_name VARCHAR(100) NOT NULL,
  middle_name VARCHAR(100),
  last_name VARCHAR(100) NOT NULL,
  second_last_name VARCHAR(100),
  email VARCHAR(180),
  phone_e164 VARCHAR(20),
  source VARCHAR(60),
  status VARCHAR(40) NOT NULL DEFAULT 'NEW',
  budget_amount NUMERIC(14,2),
  currency_code CHAR(3),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ,
  CONSTRAINT chk_lead_phone CHECK (phone_e164 IS NULL OR phone_e164 ~ '^\+[1-9][0-9]{1,14}$')
);

CREATE TABLE developments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  name VARCHAR(180) NOT NULL,
  city VARCHAR(120) NOT NULL,
  state_code VARCHAR(10) NOT NULL,
  country_code CHAR(2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE properties (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  development_id UUID REFERENCES developments(id),
  code VARCHAR(80) NOT NULL,
  property_type VARCHAR(40) NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'AVAILABLE',
  price NUMERIC(14,2) NOT NULL,
  currency_code CHAR(3) NOT NULL,
  bedrooms INT,
  bathrooms NUMERIC(4,1),
  land_area NUMERIC(10,2),
  construction_area NUMERIC(10,2),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ,
  UNIQUE(company_id, code)
);

CREATE TABLE tasks (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID REFERENCES leads(id),
  title VARCHAR(180) NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'OPEN',
  due_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE documents (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID REFERENCES leads(id),
  document_type VARCHAR(80) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE mortgage_applications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  lead_id UUID NOT NULL REFERENCES leads(id),
  mortgage_type VARCHAR(60) NOT NULL,
  status VARCHAR(60) NOT NULL DEFAULT 'NOT_STARTED',
  lender_name VARCHAR(180),
  requested_amount NUMERIC(14,2),
  currency_code CHAR(3),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE ai_conversations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company_id UUID NOT NULL REFERENCES companies(id),
  title VARCHAR(180),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE ai_messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id UUID NOT NULL REFERENCES ai_conversations(id),
  role VARCHAR(30) NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO subscription_plans(code,country_code,name_en,name_es,monthly_price,annual_price,currency_code) VALUES
('STARTER','MX','Starter MX','Inicial MX',299,2990,'MXN'),
('PRO','MX','Pro MX','Pro MX',999,9990,'MXN'),
('BUSINESS','MX','Business MX','Empresarial MX',3999,39990,'MXN'),
('STARTER','US','Starter US','Inicial US',19,190,'USD'),
('PRO','US','Pro US','Pro US',79,790,'USD'),
('BUSINESS','US','Business US','Empresarial US',249,2490,'USD');
