-- Momento del último cambio de contraseña: los tokens emitidos antes dejan de ser válidos.
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ;
