-- Suscripciones con Stripe: vínculo con el cliente/suscripción y control de webhooks repetidos.
ALTER TABLE companies
  ADD COLUMN IF NOT EXISTS stripe_customer_id VARCHAR(100),
  ADD COLUMN IF NOT EXISTS stripe_subscription_id VARCHAR(100),
  ADD COLUMN IF NOT EXISTS cancel_at_period_end BOOLEAN NOT NULL DEFAULT false;

CREATE UNIQUE INDEX IF NOT EXISTS idx_companies_stripe_customer
  ON companies(stripe_customer_id) WHERE stripe_customer_id IS NOT NULL;

-- Stripe puede reenviar el mismo evento; se registra cada uno para procesarlo una sola vez.
CREATE TABLE IF NOT EXISTS billing_webhook_events (
  event_id VARCHAR(255) PRIMARY KEY,
  provider VARCHAR(30) NOT NULL,
  event_type VARCHAR(100) NOT NULL,
  received_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
