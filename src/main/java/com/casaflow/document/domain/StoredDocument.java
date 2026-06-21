package com.casaflow.document.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "documents")
public class StoredDocument extends AuditableEntity {
    @Column(nullable = false) private UUID companyId;
    private UUID leadId;
    private UUID propertyId;
    @Column(nullable = false, length = 80) private String documentType;
    @Column(nullable = false, length = 255) private String fileName;
    @Column(nullable = false, length = 40) private String status = "PENDING";
    @Column(length = 1000) private String filePath;
    @Column(length = 180) private String contentType;
    private Long fileSize;
    @Column(columnDefinition = "text") private String notes;

    protected StoredDocument() {}

    public StoredDocument(UUID companyId, UUID leadId, UUID propertyId, String documentType, String fileName,
                          String status, String filePath, String contentType, long fileSize, String notes) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.propertyId = propertyId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.status = status;
        this.filePath = filePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.notes = notes;
    }

    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public UUID getPropertyId() { return propertyId; }
    public String getDocumentType() { return documentType; }
    public String getFileName() { return fileName; }
    public String getStatus() { return status; }
    public String getFilePath() { return filePath; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
    public String getNotes() { return notes; }
}
