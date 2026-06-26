-- Rename columns in property_images table to match entity (only if old columns exist)
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='property_images' AND COLUMN_NAME='file_path')
    EXEC sp_rename 'dbo.property_images.file_path', 'image_url', 'COLUMN';

IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='property_images' AND COLUMN_NAME='display_order')
    EXEC sp_rename 'dbo.property_images.display_order', 'sort_order', 'COLUMN';
