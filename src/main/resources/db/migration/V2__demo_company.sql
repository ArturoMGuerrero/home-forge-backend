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
) ON CONFLICT (id) DO NOTHING;
