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

-- Create documents table
CREATE TABLE documents (
    id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    company_id UNIQUEIDENTIFIER NOT NULL,
    template_id UNIQUEIDENTIFIER,
    name NVARCHAR(255) NOT NULL,
    document_type NVARCHAR(50) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    content NVARCHAR(MAX),
    file_url NVARCHAR(500),
    file_size BIGINT,
    mime_type NVARCHAR(100),
    version INT NOT NULL DEFAULT 1,
    lead_id UNIQUEIDENTIFIER,
    property_id UNIQUEIDENTIFIER,
    created_by_user_id UNIQUEIDENTIFIER,
    metadata NVARCHAR(MAX),
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    deleted_at DATETIME2,
    CONSTRAINT fk_documents_template FOREIGN KEY (template_id) REFERENCES document_templates(id),
    CONSTRAINT fk_documents_lead FOREIGN KEY (lead_id) REFERENCES leads(id),
    CONSTRAINT fk_documents_property FOREIGN KEY (property_id) REFERENCES properties(id)
);

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

-- Indexes for documents
CREATE INDEX idx_documents_company ON documents(company_id);
CREATE INDEX idx_documents_template ON documents(template_id);
CREATE INDEX idx_documents_type ON documents(document_type);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_lead ON documents(lead_id);
CREATE INDEX idx_documents_property ON documents(property_id);
CREATE INDEX idx_documents_created_by ON documents(created_by_user_id);

-- Indexes for document_signatures
CREATE INDEX idx_signatures_document ON document_signatures(document_id);
CREATE INDEX idx_signatures_company ON document_signatures(company_id);
CREATE INDEX idx_signatures_status ON document_signatures(status);
CREATE INDEX idx_signatures_email ON document_signatures(signer_email);
