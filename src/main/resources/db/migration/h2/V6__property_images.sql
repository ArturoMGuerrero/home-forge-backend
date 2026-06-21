CREATE TABLE property_images (
  id UUID DEFAULT random_uuid() PRIMARY KEY,
  property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
  image_url VARCHAR(1000) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(property_id, sort_order)
);

CREATE INDEX idx_property_images_property_id ON property_images(property_id);

INSERT INTO property_images(property_id, image_url, sort_order)
SELECT id, image_url, 0
FROM properties
WHERE image_url IS NOT NULL AND image_url <> '';
