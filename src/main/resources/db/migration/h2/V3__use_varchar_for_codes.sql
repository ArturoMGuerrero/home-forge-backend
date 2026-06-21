ALTER TABLE companies
  ALTER COLUMN country_code SET DATA TYPE VARCHAR(2);
ALTER TABLE companies
  ALTER COLUMN default_currency SET DATA TYPE VARCHAR(3);

ALTER TABLE subscription_plans
  ALTER COLUMN country_code SET DATA TYPE VARCHAR(2);
ALTER TABLE subscription_plans
  ALTER COLUMN currency_code SET DATA TYPE VARCHAR(3);

ALTER TABLE leads
  ALTER COLUMN currency_code SET DATA TYPE VARCHAR(3);

ALTER TABLE developments
  ALTER COLUMN country_code SET DATA TYPE VARCHAR(2);

ALTER TABLE properties
  ALTER COLUMN currency_code SET DATA TYPE VARCHAR(3);

ALTER TABLE mortgage_applications
  ALTER COLUMN currency_code SET DATA TYPE VARCHAR(3);
