-- Agregar columnas de coordenadas geográficas a la tabla properties
ALTER TABLE properties ADD COLUMN latitude DECIMAL(9, 6);
ALTER TABLE properties ADD COLUMN longitude DECIMAL(10, 6);

-- Crear índice para búsquedas por coordenadas (útil para búsquedas geográficas)
CREATE INDEX idx_properties_coordinates ON properties(latitude, longitude);
