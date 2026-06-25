-- Agregar columnas de coordenadas geográficas a la tabla properties
ALTER TABLE properties
ADD COLUMN latitude DECIMAL(9, 6),
ADD COLUMN longitude DECIMAL(10, 6);

-- Crear índice para búsquedas por coordenadas (útil para búsquedas geográficas)
CREATE INDEX idx_properties_coordinates ON properties(latitude, longitude)
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Comentarios para documentación
COMMENT ON COLUMN properties.latitude IS 'Latitud de la ubicación de la propiedad (decimal degrees, WGS84)';
COMMENT ON COLUMN properties.longitude IS 'Longitud de la ubicación de la propiedad (decimal degrees, WGS84)';
