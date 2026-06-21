-- Change assigned_to from uniqueidentifier to nvarchar
-- First check if column exists and is uniqueidentifier
IF EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME='leads'
    AND COLUMN_NAME='assigned_to'
    AND DATA_TYPE='uniqueidentifier'
)
BEGIN
    -- Drop the column and recreate it as nvarchar
    ALTER TABLE leads DROP COLUMN assigned_to;
    ALTER TABLE leads ADD assigned_to NVARCHAR(180);
END
