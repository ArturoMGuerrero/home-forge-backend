package com.homeforge.payment.dto;

public record PaymentLinkResponse(
        String initPoint,
        String preferenceId,
        String sandboxInitPoint
) {}
