package com.casaflow.payment.dto;

public record PaymentLinkResponse(
        String initPoint,
        String preferenceId,
        String sandboxInitPoint
) {}
