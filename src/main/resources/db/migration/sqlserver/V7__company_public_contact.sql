-- SQL Server version

-- Add public contact columns
ALTER TABLE companies ADD public_email NVARCHAR(180);
ALTER TABLE companies ADD public_phone_e164 NVARCHAR(20);
