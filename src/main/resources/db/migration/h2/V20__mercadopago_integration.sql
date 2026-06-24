-- Agregar campos para integración con Mercado Pago (H2)
ALTER TABLE companies
  ADD COLUMN mercadopago_customer_id VARCHAR(100);

ALTER TABLE companies
  ADD COLUMN mercadopago_subscription_id VARCHAR(100);

ALTER TABLE companies
  ADD COLUMN payment_method VARCHAR(50);

ALTER TABLE companies
  ADD COLUMN last_payment_at TIMESTAMPTZ;

ALTER TABLE companies
  ADD COLUMN last_payment_status VARCHAR(50);

-- Índice para búsquedas por customer_id
CREATE INDEX idx_companies_mp_customer
  ON companies(mercadopago_customer_id);

-- Índice para búsquedas por subscription_id
CREATE INDEX idx_companies_mp_subscription
  ON companies(mercadopago_subscription_id);
