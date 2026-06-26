-- Agregar columnas de coordenadas geográficas a la tabla properties
-- Note: Using IF NOT EXISTS to avoid errors if columns already exist from V17
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='latitude')
    ALTER TABLE properties ADD latitude DECIMAL(9, 6);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='properties' AND COLUMN_NAME='longitude')
    ALTER TABLE properties ADD longitude DECIMAL(10, 6);

-- Crear índice para búsquedas por coordenadas (útil para búsquedas geográficas)
CREATE INDEX idx_properties_coordinates ON properties(latitude, longitude)
WHERE (latitude IS NOT NULL AND longitude IS NOT NULL);
