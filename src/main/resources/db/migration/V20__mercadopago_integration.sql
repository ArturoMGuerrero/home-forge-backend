-- Agregar campos para integración con Mercado Pago (PostgreSQL)
ALTER TABLE companies
  ADD COLUMN mercadopago_customer_id VARCHAR(100),
  ADD COLUMN mercadopago_subscription_id VARCHAR(100),
  ADD COLUMN payment_method VARCHAR(50),
  ADD COLUMN last_payment_at TIMESTAMPTZ,
  ADD COLUMN last_payment_status VARCHAR(50);

-- Índice para búsquedas por customer_id
CREATE INDEX idx_companies_mp_customer
  ON companies(mercadopago_customer_id)
  WHERE mercadopago_customer_id IS NOT NULL;

-- Índice para búsquedas por subscription_id
CREATE INDEX idx_companies_mp_subscription
  ON companies(mercadopago_subscription_id)
  WHERE mercadopago_subscription_id IS NOT NULL;
