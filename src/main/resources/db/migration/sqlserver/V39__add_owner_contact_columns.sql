-- Agregar columnas de información de contacto del propietario

ALTER TABLE properties ADD owner_name NVARCHAR(150) NULL;
ALTER TABLE properties ADD owner_email NVARCHAR(100) NULL;
ALTER TABLE properties ADD owner_phone NVARCHAR(20) NULL;
ALTER TABLE properties ADD owner_phone_secondary NVARCHAR(20) NULL;
ALTER TABLE properties ADD owner_notes NVARCHAR(MAX) NULL;
