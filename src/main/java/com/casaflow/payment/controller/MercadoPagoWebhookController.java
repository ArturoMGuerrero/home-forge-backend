package com.casaflow.payment.controller;

import com.casaflow.payment.service.MercadoPagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/mercadopago")
public class MercadoPagoWebhookController {
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoWebhookController.class);
    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoWebhookController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    /**
     * Webhook que recibe notificaciones de Mercado Pago
     *
     * Tipo de notificación:
     * - payment: Pago procesado (aprobado, rechazado, pendiente)
     */
    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("📩 Webhook recibido de Mercado Pago: {}", payload);

            String action = (String) payload.get("action");
            String type = (String) payload.get("type");

            // Procesar notificaciones de pago
            if ("payment".equals(type)) {
                handlePaymentNotification(payload, action);
            } else {
                logger.warn("⚠️ Tipo de webhook no soportado: {}", type);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("❌ Error procesando webhook de Mercado Pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Maneja notificaciones de pagos
     * Se ejecuta cuando un pago es procesado (aprobado, rechazado, etc.)
     */
    private void handlePaymentNotification(Map<String, Object> payload, String action) {
        try {
            if (!"payment.created".equals(action) && !"payment.updated".equals(action)) {
                return;
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String paymentId = String.valueOf(data.get("id"));

            logger.info("💰 Pago recibido - ID: {}, Acción: {}", paymentId, action);

            mercadoPagoService.processPayment(paymentId);

            logger.info("✅ Pago {} procesado correctamente", paymentId);

        } catch (Exception e) {
            logger.error("❌ Error procesando pago", e);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testWebhook() {
        logger.info("✅ Test endpoint called");
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Webhook endpoint is working",
                "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
