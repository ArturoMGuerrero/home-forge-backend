package com.casaflow.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull(message = "Company ID es requerido")
        UUID companyId,

        @NotNull(message = "Plan code es requerido")
        String planCode,

        String payerEmail
) {}
