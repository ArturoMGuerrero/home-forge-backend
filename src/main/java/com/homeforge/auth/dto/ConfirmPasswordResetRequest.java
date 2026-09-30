package com.homeforge.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmPasswordResetRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword
) {
}
