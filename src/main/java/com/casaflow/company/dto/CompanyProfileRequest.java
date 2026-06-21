package com.casaflow.company.dto;

import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyProfileRequest(
        @NotBlank @Size(max = 180) String name,
        @NotBlank @Pattern(regexp = Validations.COUNTRY_CODE) String countryCode,
        @NotBlank @Size(max = 80) String stateCode,
        @Size(max = 120) String city,
        @Size(max = 255) String address,
        @Size(max = 20) String postalCode,
        @Email @Size(max = 180) String publicEmail,
        @Pattern(regexp = "^$|" + Validations.PHONE_E164, message = "must use E.164 format, for example +524421234567")
        String publicPhoneE164,
        @Size(max = 500) @Pattern(regexp = "^$|https?://.+", message = "must start with http:// or https://") String websiteUrl,
        @Size(max = 5000) String publicDescription,
        @Size(max = 3000) String mission,
        @Size(max = 3000) String vision,
        @Size(max = 120) String professionalLicense,
        @Min(0) @Max(300) Integer yearsExperience
) {
}
