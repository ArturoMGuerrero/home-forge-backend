package com.casaflow.document.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "document_templates")
public class DocumentTemplate extends AuditableEntity {
    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentType documentType;

    @Column(length = 50)
    private String category;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String variables;

    @Column(nullable = false)
    private boolean isDefault = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int version = 1;

    protected DocumentTemplate() {}

    public DocumentTemplate(
        UUID companyId,
        String name,
        String description,
        DocumentType documentType,
        String content
    ) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.documentType = documentType;
        this.content = content;
    }

    public UUID getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public DocumentType getDocumentType() { return documentType; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getVariables() { return variables; }
    public boolean isDefault() { return isDefault; }
    public boolean isActive() { return active; }
    public int getVersion() { return version; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setContent(String content) { this.content = content; }
    public void setVariables(String variables) { this.variables = variables; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public void setActive(boolean active) { this.active = active; }
    public void incrementVersion() { this.version++; }
}
