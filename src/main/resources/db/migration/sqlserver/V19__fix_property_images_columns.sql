-- Rename columns in property_images table to match entity
EXEC sp_rename 'property_images.file_path', 'image_url', 'COLUMN';
EXEC sp_rename 'property_images.display_order', 'sort_order', 'COLUMN';
