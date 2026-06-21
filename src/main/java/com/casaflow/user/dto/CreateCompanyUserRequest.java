package com.casaflow.user.dto;

import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCompanyUserRequest(
        @NotNull UUID companyId,
        @NotNull UUID requesterUserId,
        @NotBlank @Size(max=180) String fullName,
        @NotBlank @Email @Size(max=180) String email,
        @Pattern(regexp="^$|" + Validations.PHONE_E164) String phoneE164,
        @NotBlank @Pattern(regexp="ADMIN|AGENT") String role,
        @NotBlank @Size(min=8, max=72) String password
) {
}
