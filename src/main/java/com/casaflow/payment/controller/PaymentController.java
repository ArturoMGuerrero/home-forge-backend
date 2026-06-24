package com.casaflow.payment.controller;

import com.casaflow.payment.dto.CreateSubscriptionRequest;
import com.casaflow.payment.dto.PaymentLinkResponse;
import com.casaflow.payment.dto.PaymentStatusResponse;
import com.casaflow.payment.service.MercadoPagoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final MercadoPagoService mercadoPagoService;

    public PaymentController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/subscriptions")
    public PaymentLinkResponse createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        return mercadoPagoService.createSubscription(
                request.companyId(),
                request.planCode(),
                request.payerEmail()
        );
    }

    @GetMapping("/status")
    public PaymentStatusResponse getPaymentStatus(@RequestParam UUID companyId) {
        return mercadoPagoService.getPaymentStatus(companyId);
    }
}
