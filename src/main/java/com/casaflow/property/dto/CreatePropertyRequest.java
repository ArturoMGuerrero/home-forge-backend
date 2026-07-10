package com.casaflow.property.dto;

import com.casaflow.property.domain.ListingType;
import com.casaflow.property.domain.PropertyStatus;
import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePropertyRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 80) String code,
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 40) String propertyType,
        @NotNull ListingType listingType,
        @NotNull PropertyStatus status,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal price,
        @NotBlank @Pattern(regexp = Validations.ISO_CURRENCY) String currencyCode,
        @NotBlank @Pattern(regexp = Validations.COUNTRY_CODE) String countryCode,
        @NotBlank @Size(max = 80) String stateCode,
        @NotBlank @Size(max = 120) String city,
        @Size(max = 255) String address,
        @Digits(integer = 2, fraction = 6) BigDecimal latitude,
        @Digits(integer = 3, fraction = 6) BigDecimal longitude,
        @Min(0) @Max(100) Integer bedrooms,
        @DecimalMin("0.0") @Max(100) @Digits(integer = 3, fraction = 1) BigDecimal bathrooms,
        @DecimalMin("0.0") @Digits(integer = 8, fraction = 2) BigDecimal landArea,
        @DecimalMin("0.0") @Digits(integer = 8, fraction = 2) BigDecimal constructionArea,
        @Min(0) @Max(100) Integer parkingSpaces,
        @Size(max = 5000) String description,
        @Size(max = 1000) String imageUrl,
        boolean published,
        // Owner contact information
        @Size(max = 150) String ownerName,
        @Size(max = 100) String ownerEmail,
        @Size(max = 20) String ownerPhone,
        @Size(max = 20) String ownerPhoneSecondary,
        @Size(max = 5000) String ownerNotes
) {
}
