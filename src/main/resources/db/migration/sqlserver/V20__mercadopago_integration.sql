-- Agregar campos para integración con Mercado Pago
ALTER TABLE companies ADD mercado_pago_customer_id VARCHAR(100);
ALTER TABLE companies ADD mercado_pago_subscription_id VARCHAR(100);
ALTER TABLE companies ADD payment_method VARCHAR(50);
ALTER TABLE companies ADD last_payment_at DATETIMEOFFSET;
ALTER TABLE companies ADD last_payment_status VARCHAR(50);
