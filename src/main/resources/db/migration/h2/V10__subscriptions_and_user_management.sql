ALTER TABLE companies
  ADD COLUMN plan_code VARCHAR(20) NOT NULL DEFAULT 'STARTER';
ALTER TABLE companies
  ADD COLUMN subscription_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
  ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;

UPDATE companies
SET plan_code = 'STARTER',
    subscription_status = 'ACTIVE'
WHERE id = '8e77eb53-b2ca-44ff-a1f7-28ad225eb144';
