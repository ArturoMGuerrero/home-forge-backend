CREATE TABLE property_images (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
  image_url VARCHAR(1000) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(property_id, sort_order)
);

CREATE INDEX idx_property_images_property_id ON property_images(property_id);

INSERT INTO property_images(property_id, image_url, sort_order)
SELECT id, image_url, 0
FROM properties
WHERE image_url IS NOT NULL AND image_url <> '';
