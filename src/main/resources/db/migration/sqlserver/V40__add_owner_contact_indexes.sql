-- Crear índices para información de contacto del propietario

CREATE INDEX idx_properties_owner_email ON properties(owner_email) WHERE owner_email IS NOT NULL;
CREATE INDEX idx_properties_owner_phone ON properties(owner_phone) WHERE owner_phone IS NOT NULL;
