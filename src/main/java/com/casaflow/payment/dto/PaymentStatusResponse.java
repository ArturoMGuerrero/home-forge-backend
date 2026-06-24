package com.casaflow.payment.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentStatusResponse(
        UUID companyId,
        String planCode,
        String subscriptionStatus,
        String paymentMethod,
        String lastPaymentStatus,
        Instant lastPaymentAt,
        Instant nextBillingAt,
        boolean hasActiveSubscription,
        String mercadoPagoSubscriptionId
) {}
