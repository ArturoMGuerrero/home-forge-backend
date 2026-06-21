ALTER TABLE companies
  ADD COLUMN public_email VARCHAR(180);
ALTER TABLE companies
  ADD COLUMN public_phone_e164 VARCHAR(20);

UPDATE companies
SET public_email = (
  SELECT email
  FROM users
  WHERE users.company_id = companies.id
    AND users.deleted_at IS NULL
  ORDER BY users.created_at ASC
  LIMIT 1
),
public_phone_e164 = (
  SELECT phone_e164
  FROM users
  WHERE users.company_id = companies.id
    AND users.deleted_at IS NULL
  ORDER BY users.created_at ASC
  LIMIT 1
);

ALTER TABLE companies
  ADD CONSTRAINT chk_company_public_phone
  CHECK (public_phone_e164 IS NULL OR public_phone_e164 REGEXP '^\+[1-9][0-9]{1,14}$');
