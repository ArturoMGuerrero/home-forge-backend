-- Agregar columnas de coordenadas geográficas a la tabla properties
ALTER TABLE properties ADD latitude DECIMAL(9, 6);
ALTER TABLE properties ADD longitude DECIMAL(10, 6);

-- Crear índice para búsquedas por coordenadas (útil para búsquedas geográficas)
CREATE INDEX idx_properties_coordinates ON properties(latitude, longitude)
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;
