-- SQL Server version

ALTER TABLE companies ADD trial_started_at DATETIME2;
ALTER TABLE companies ADD trial_ends_at DATETIME2;
ALTER TABLE companies ADD next_billing_at DATETIME2;
GO

UPDATE companies
SET trial_started_at = GETDATE(),
    trial_ends_at = DATEADD(DAY, 14, GETDATE()),
    subscription_status = 'TRIAL'
WHERE trial_started_at IS NULL;
