package com.casaflow.document.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document extends AuditableEntity {
    @Column(nullable = false)
    private UUID companyId;

    private UUID templateId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentStatus status = DocumentStatus.DRAFT;

    @Column(columnDefinition = "text")
    private String content;

    @Column(length = 500)
    private String fileUrl;

    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column(nullable = false)
    private int version = 1;

    private UUID leadId;

    private UUID propertyId;

    private UUID createdByUserId;

    @Column(columnDefinition = "text")
    private String metadata;

    protected Document() {}

    public Document(
        UUID companyId,
        String name,
        DocumentType documentType,
        UUID createdByUserId
    ) {
        this.companyId = companyId;
        this.name = name;
        this.documentType = documentType;
        this.createdByUserId = createdByUserId;
    }

    public UUID getCompanyId() { return companyId; }
    public UUID getTemplateId() { return templateId; }
    public String getName() { return name; }
    public DocumentType getDocumentType() { return documentType; }
    public DocumentStatus getStatus() { return status; }
    public String getContent() { return content; }
    public String getFileUrl() { return fileUrl; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public int getVersion() { return version; }
    public UUID getLeadId() { return leadId; }
    public UUID getPropertyId() { return propertyId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public String getMetadata() { return metadata; }

    public void setTemplateId(UUID templateId) { this.templateId = templateId; }
    public void setName(String name) { this.name = name; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public void setContent(String content) { this.content = content; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setLeadId(UUID leadId) { this.leadId = leadId; }
    public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public void incrementVersion() { this.version++; }
}
