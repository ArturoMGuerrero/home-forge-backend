-- SQL Server version

ALTER TABLE companies
  ALTER COLUMN country_code TYPE NVARCHAR(2),
  ALTER COLUMN default_currency TYPE NVARCHAR(3);

ALTER TABLE subscription_plans
  ALTER COLUMN country_code TYPE NVARCHAR(2),
  ALTER COLUMN currency_code TYPE NVARCHAR(3);

ALTER TABLE leads
  ALTER COLUMN currency_code TYPE NVARCHAR(3);

ALTER TABLE developments
  ALTER COLUMN country_code TYPE NVARCHAR(2);

ALTER TABLE properties
  ALTER COLUMN currency_code TYPE NVARCHAR(3);

ALTER TABLE mortgage_applications
  ALTER COLUMN currency_code TYPE NVARCHAR(3);
