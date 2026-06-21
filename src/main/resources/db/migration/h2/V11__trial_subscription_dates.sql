ALTER TABLE companies
  ADD COLUMN trial_started_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE companies
  ADD COLUMN trial_ends_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE companies
  ADD COLUMN next_billing_at TIMESTAMP WITH TIME ZONE;

UPDATE companies
SET trial_started_at = CURRENT_TIMESTAMP,
    trial_ends_at = DATEADD('DAY', 14, CURRENT_TIMESTAMP),
    subscription_status = 'TRIAL'
WHERE trial_started_at IS NULL;
