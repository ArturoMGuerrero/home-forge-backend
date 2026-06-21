package com.casaflow.auth.dto;

import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 180) String fullName,
        @NotBlank @Size(max = 180) String companyName,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Pattern(regexp = Validations.PHONE_E164, message = "must use E.164 format, for example +524421234567") String phoneE164,
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
