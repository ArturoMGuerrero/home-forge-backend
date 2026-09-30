package com.casaflow.payment.controller;

import com.casaflow.payment.dto.CreateSubscriptionRequest;
import com.casaflow.payment.dto.PaymentLinkResponse;
import com.casaflow.payment.dto.PaymentStatusResponse;
import com.casaflow.payment.service.MercadoPagoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    /**
     * Endpoint para procesar pagos cuando el usuario vuelve del checkout
     * El frontend llama esto después de que MercadoPago redirige al usuario
     */
    @PostMapping("/process/{paymentId}")
    public ResponseEntity<Map<String, String>> processPaymentFromFrontend(@PathVariable String paymentId) {
        try {
            mercadoPagoService.processPayment(paymentId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pago procesado correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
