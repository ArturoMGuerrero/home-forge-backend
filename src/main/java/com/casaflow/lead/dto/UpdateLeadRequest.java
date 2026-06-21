package com.casaflow.lead.dto;

import com.casaflow.lead.domain.LeadPriority;
import com.casaflow.lead.domain.LeadStatus;
import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UpdateLeadRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max=100) String firstName,
        @NotBlank @Size(max=100) String lastName,
        @Email @Size(max=180) String email,
        @Pattern(regexp = "^$|" + Validations.PHONE_E164) String phoneE164,
        @Size(max=60) String source,
        @NotNull LeadStatus status,
        @Pattern(regexp = "^(SALE|RENT|Venta|Renta)?$") String listingType,
        @DecimalMin("0.0") @Digits(integer = 12, fraction = 2) BigDecimal budgetMin,
        @DecimalMin("0.0") @Digits(integer = 12, fraction = 2) BigDecimal budgetMax,
        @Pattern(regexp = "^$|" + Validations.ISO_CURRENCY) String currencyCode,
        @Pattern(regexp = "^$|" + Validations.COUNTRY_CODE) String countryCode,
        @Size(max=80) String stateCode,
        @Size(max=120) String city,
        @Size(max=40) String propertyType,
        @Min(0) @Max(100) Integer bedroomsMin,
        @DecimalMin("0.0") @Max(100) @Digits(integer = 3, fraction = 1) BigDecimal bathroomsMin,
        @Size(max=40) String financingType,
        @NotNull LeadPriority priority,
        @Size(max=180) String assignedTo,
        Instant nextFollowUpAt,
        @Size(max=5000) String notes
) {
}
