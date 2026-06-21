-- Add missing columns to companies table
ALTER TABLE companies ADD city NVARCHAR(120);
ALTER TABLE companies ADD postal_code NVARCHAR(20);
ALTER TABLE companies ADD public_description NVARCHAR(MAX);
ALTER TABLE companies ADD mission NVARCHAR(MAX);
ALTER TABLE companies ADD vision NVARCHAR(MAX);
ALTER TABLE companies ADD professional_license NVARCHAR(120);
ALTER TABLE companies ADD years_experience INT;
ALTER TABLE companies ADD plan_code NVARCHAR(20) NOT NULL DEFAULT 'STARTER';
ALTER TABLE companies ADD subscription_status NVARCHAR(20) NOT NULL DEFAULT 'TRIAL';
ALTER TABLE companies ADD trial_started_at DATETIME2;
ALTER TABLE companies ADD trial_ends_at DATETIME2;
ALTER TABLE companies ADD next_billing_at DATETIME2;

-- Rename public_phone to public_phone_e164 if it exists
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'companies' AND COLUMN_NAME = 'public_phone')
BEGIN
    EXEC sp_rename 'companies.public_phone', 'public_phone_e164', 'COLUMN';
END
