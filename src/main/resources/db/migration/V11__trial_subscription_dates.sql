ALTER TABLE companies
  ADD COLUMN trial_started_at TIMESTAMPTZ,
  ADD COLUMN trial_ends_at TIMESTAMPTZ,
  ADD COLUMN next_billing_at TIMESTAMPTZ;

UPDATE companies
SET trial_started_at = CURRENT_TIMESTAMP,
    trial_ends_at = CURRENT_TIMESTAMP + INTERVAL '14 days',
    subscription_status = 'TRIAL'
WHERE trial_started_at IS NULL;
