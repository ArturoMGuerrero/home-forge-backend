package com.casaflow.document.dto;

import com.casaflow.document.domain.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTemplateRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 5000) String description,
        @NotNull DocumentType documentType,
        @Size(max = 50) String category,
        @NotBlank String content,
        String variables,
        Boolean isDefault
) {
}
