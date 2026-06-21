-- SQL Server version

ALTER TABLE companies
  ADD public_email NVARCHAR(180),
  ADD public_phone_e164 NVARCHAR(20);

UPDATE companies company
SET public_email = contact.email,
    public_phone_e164 = contact.phone_e164
FROM (
  SELECT DISTINCT ON (company_id)
    company_id,
    email,
    phone_e164
  FROM users
  WHERE deleted_at IS NULL
  ORDER BY company_id, created_at ASC
) contact
WHERE company.id = contact.company_id;

ALTER TABLE companies
  ADD CONSTRAINT chk_company_public_phone
  CHECK (public_phone_e164 IS NULL OR public_phone_e164 LIKE '+[1-9]%');
