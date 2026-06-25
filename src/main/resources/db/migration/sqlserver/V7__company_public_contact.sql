-- SQL Server version

-- Add columns separately
ALTER TABLE companies ADD public_email NVARCHAR(180);
ALTER TABLE companies ADD public_phone_e164 NVARCHAR(20);

-- Update with first user's contact info (using ROW_NUMBER window function)
UPDATE companies
SET public_email = contact.email,
    public_phone_e164 = contact.phone_e164
FROM companies
INNER JOIN (
  SELECT company_id,
    email,
    phone_e164,
    ROW_NUMBER() OVER (PARTITION BY company_id ORDER BY created_at ASC) as rn
  FROM users
  WHERE deleted_at IS NULL
) contact ON companies.id = contact.company_id
WHERE contact.rn = 1;

-- Add constraint
ALTER TABLE companies ADD CONSTRAINT chk_company_public_phone CHECK (public_phone_e164 IS NULL OR public_phone_e164 LIKE '+[1-9]%');
