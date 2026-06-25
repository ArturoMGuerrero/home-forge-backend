-- SQL Server version

ALTER TABLE companies ADD plan_code NVARCHAR(20) NOT NULL DEFAULT 'STARTER';
ALTER TABLE companies ADD subscription_status NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users
  ADD is_active BIT NOT NULL DEFAULT 1;

UPDATE companies
SET plan_code = 'STARTER',
    subscription_status = 'ACTIVE'
WHERE id = '8e77eb53-b2ca-44ff-a1f7-28ad225eb144';
