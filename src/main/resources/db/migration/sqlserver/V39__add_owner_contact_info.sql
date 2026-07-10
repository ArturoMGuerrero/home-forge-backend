-- Agregar información de contacto del propietario en properties
ALTER TABLE properties ADD owner_name NVARCHAR(150) NULL;
ALTER TABLE properties ADD owner_email NVARCHAR(100) NULL;
ALTER TABLE properties ADD owner_phone NVARCHAR(20) NULL;
ALTER TABLE properties ADD owner_phone_secondary NVARCHAR(20) NULL;
ALTER TABLE properties ADD owner_notes NVARCHAR(MAX) NULL;

-- Índices para búsquedas
CREATE INDEX idx_properties_owner_email ON properties(owner_email) WHERE owner_email IS NOT NULL;
CREATE INDEX idx_properties_owner_phone ON properties(owner_phone) WHERE owner_phone IS NOT NULL;

EXEC sp_addextendedproperty
    @name = N'MS_Description', @value = 'Nombre del propietario de la propiedad',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = 'properties',
    @level2type = N'COLUMN', @level2name = 'owner_name';

EXEC sp_addextendedproperty
    @name = N'MS_Description', @value = 'Email del propietario para contacto',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = 'properties',
    @level2type = N'COLUMN', @level2name = 'owner_email';

EXEC sp_addextendedproperty
    @name = N'MS_Description', @value = 'Teléfono principal del propietario',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = 'properties',
    @level2type = N'COLUMN', @level2name = 'owner_phone';

EXEC sp_addextendedproperty
    @name = N'MS_Description', @value = 'Teléfono secundario del propietario',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = 'properties',
    @level2type = N'COLUMN', @level2name = 'owner_phone_secondary';

EXEC sp_addextendedproperty
    @name = N'MS_Description', @value = 'Notas adicionales sobre el propietario',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = 'properties',
    @level2type = N'COLUMN', @level2name = 'owner_notes';
