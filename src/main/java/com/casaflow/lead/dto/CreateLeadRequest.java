package com.casaflow.lead.dto;
import com.casaflow.shared.validation.Validations;
import jakarta.validation.constraints.*;
import java.util.UUID;
import java.math.BigDecimal;
public record CreateLeadRequest(
  @NotNull UUID companyId,
  @NotBlank @Size(max=100) String firstName,
  @NotBlank @Size(max=100) String lastName,
  @Email @Size(max=180) String email,
  @Pattern(regexp = "^$|" + Validations.PHONE_E164, message = "must use E.164 format, for example +19155551234") String phoneE164,
  @Pattern(regexp = "^(SALE|RENT|Venta|Renta)?$") String listingType,
  @DecimalMin("0.0") @Digits(integer = 12, fraction = 2) BigDecimal budgetMax,
  @Pattern(regexp = "^$|" + Validations.ISO_CURRENCY) String currencyCode,
  @Size(max=120) String city
) {}
