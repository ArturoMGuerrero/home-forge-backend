-- SQL Server version

ALTER TABLE companies
  ADD trial_started_at DATETIME2,
  ADD trial_ends_at DATETIME2,
  ADD next_billing_at DATETIME2;

UPDATE companies
SET trial_started_at = CURRENT_TIMESTAMP,
    trial_ends_at = CURRENT_TIMESTAMP + INTERVAL '14 days',
    subscription_status = 'TRIAL'
WHERE trial_started_at IS NULL;
