-- SQL Server version

IF NOT EXISTS (SELECT 1 FROM companies WHERE id = '00000000-0000-0000-0000-000000000001')
BEGIN
  INSERT INTO companies (
    id,
    name,
    country_code,
    state_code,
    default_language,
    default_currency,
    timezone
  ) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'CasaFlow Demo',
    'MX',
    'QRO',
    'es',
    'MXN',
    'America/Mexico_City'
  );
END
