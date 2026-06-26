package com.casaflow.document.dto;

import com.casaflow.document.domain.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record CreateDocumentRequest(
        @NotNull UUID companyId,
        UUID templateId,
        @NotBlank @Size(max = 255) String name,
        @NotNull DocumentType documentType,
        @NotNull UUID createdByUserId,
        UUID leadId,
        UUID propertyId,
        Map<String, String> variables,
        String metadata
) {
}
