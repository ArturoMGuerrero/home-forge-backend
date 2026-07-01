-- Create document_templates table
CREATE TABLE document_templates (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    document_type NVARCHAR(50) NOT NULL,
    category NVARCHAR(50),
    content NVARCHAR(MAX) NOT NULL,
    variables NVARCHAR(MAX),
    is_default BIT NOT NULL DEFAULT 0,
    active BIT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted_at DATETIME2
);

-- Add new columns to existing documents table
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='template_id')
    ALTER TABLE documents ADD template_id UNIQUEIDENTIFIER;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='name')
    ALTER TABLE documents ADD name NVARCHAR(255);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='content')
    ALTER TABLE documents ADD content NVARCHAR(MAX);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='file_url')
    ALTER TABLE documents ADD file_url NVARCHAR(500);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='mime_type')
    ALTER TABLE documents ADD mime_type NVARCHAR(100);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='version')
    ALTER TABLE documents ADD version INT NOT NULL DEFAULT 1;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='created_by_user_id')
    ALTER TABLE documents ADD created_by_user_id UNIQUEIDENTIFIER;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='metadata')
    ALTER TABLE documents ADD metadata NVARCHAR(MAX);

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='documents' AND COLUMN_NAME='updated_at')
    ALTER TABLE documents ADD updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE();
GO

-- Add foreign key constraints if they don't exist
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name='fk_documents_template')
    ALTER TABLE documents ADD CONSTRAINT fk_documents_template FOREIGN KEY (template_id) REFERENCES document_templates(id);

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name='fk_documents_property')
    ALTER TABLE documents ADD CONSTRAINT fk_documents_property FOREIGN KEY (property_id) REFERENCES properties(id);

-- Create document_signatures table
CREATE TABLE document_signatures (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    document_id UNIQUEIDENTIFIER NOT NULL,
    signer_name NVARCHAR(255) NOT NULL,
    signer_email NVARCHAR(180) NOT NULL,
    signer_role NVARCHAR(50),
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    signature_data NVARCHAR(MAX),
    ip_address NVARCHAR(45),
    user_agent NVARCHAR(500),
    signed_at DATETIME2,
    sent_at DATETIME2,
    expires_at DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_signatures_document FOREIGN KEY (document_id) REFERENCES documents(id)
);

-- Indexes for document_templates
CREATE INDEX idx_document_templates_company ON document_templates(company_id);
CREATE INDEX idx_document_templates_type ON document_templates(document_type);
CREATE INDEX idx_document_templates_active ON document_templates(active);

-- Indexes for documents (only create if they don't exist)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_documents_template' AND object_id = OBJECT_ID('documents'))
    CREATE INDEX idx_documents_template ON documents(template_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_documents_created_by' AND object_id = OBJECT_ID('documents'))
    CREATE INDEX idx_documents_created_by ON documents(created_by_user_id);

-- Indexes for document_signatures
CREATE INDEX idx_signatures_document ON document_signatures(document_id);
CREATE INDEX idx_signatures_company ON document_signatures(company_id);
CREATE INDEX idx_signatures_status ON document_signatures(status);
CREATE INDEX idx_signatures_email ON document_signatures(signer_email);
