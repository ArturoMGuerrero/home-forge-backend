-- SQL Server version

ALTER TABLE companies ALTER COLUMN country_code NVARCHAR(2);
ALTER TABLE companies ALTER COLUMN default_currency NVARCHAR(3);

ALTER TABLE subscription_plans ALTER COLUMN country_code NVARCHAR(2);
ALTER TABLE subscription_plans ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE leads ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE developments ALTER COLUMN country_code NVARCHAR(2);

ALTER TABLE properties ALTER COLUMN currency_code NVARCHAR(3);

ALTER TABLE mortgage_applications ALTER COLUMN currency_code NVARCHAR(3);
