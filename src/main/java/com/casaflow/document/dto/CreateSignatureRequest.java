package com.casaflow.document.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSignatureRequest(
        @NotNull UUID companyId,
        @NotNull UUID documentId,
        @NotBlank @Size(max = 255) String signerName,
        @NotBlank @Email @Size(max = 180) String signerEmail,
        @Size(max = 50) String signerRole
) {
}
