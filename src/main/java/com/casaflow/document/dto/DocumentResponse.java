package com.casaflow.document.dto;

import com.casaflow.document.domain.StoredDocument;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        UUID leadId,
        String leadName,
        UUID propertyId,
        String propertyTitle,
        String documentType,
        String fileName,
        String status,
        String contentType,
        Long fileSize,
        String notes,
        Instant createdAt
) {
    public static DocumentResponse of(StoredDocument document, String leadName, String propertyTitle) {
        return new DocumentResponse(document.getId(), document.getLeadId(), leadName, document.getPropertyId(),
                propertyTitle, document.getDocumentType(), document.getFileName(), document.getStatus(),
                document.getContentType(), document.getFileSize(), document.getNotes(), document.getCreatedAt());
    }
}
