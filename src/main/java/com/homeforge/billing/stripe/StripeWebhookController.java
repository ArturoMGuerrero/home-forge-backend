package com.homeforge.billing.stripe;

import com.homeforge.billing.BillingWebhookEvent;
import com.homeforge.billing.BillingWebhookEventRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Avisos de Stripe. Solo se aceptan si la firma corresponde a STRIPE_WEBHOOK_SECRET; el estado de la
 * suscripción se vuelve a consultar en Stripe en lugar de confiar en el contenido del aviso.
 */
@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final StripeBillingProvider stripe;
    private final BillingWebhookEventRepository processedEvents;
    private final String webhookSecret;

    public StripeWebhookController(
            StripeBillingProvider stripe,
            BillingWebhookEventRepository processedEvents,
            @Value("${stripe.webhook-secret:}") String webhookSecret
    ) {
        this.stripe = stripe;
        this.processedEvents = processedEvents;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> receive(@RequestBody String payload,
                                        @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        if (webhookSecret.isBlank() || !stripe.isConfigured()) {
            log.warn("Stripe: llegó un webhook pero STRIPE_WEBHOOK_SECRET no está configurado");
            return ResponseEntity.status(503).build();
        }
        if (signature == null || signature.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Event event;
        try {
            event = stripe.client().constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException | IllegalArgumentException ex) {
            log.warn("Stripe: webhook con firma inválida rechazado");
            return ResponseEntity.badRequest().build();
        }

        if (processedEvents.existsById(event.getId())) {
            return ResponseEntity.ok().build();
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) object(event);
                if (session.getClientReferenceId() != null && session.getCustomer() != null) {
                    stripe.linkCustomer(session.getClientReferenceId(), session.getCustomer());
                }
                if (session.getSubscription() != null) {
                    stripe.syncSubscription(session.getSubscription());
                }
            }
            case "customer.subscription.created", "customer.subscription.updated", "customer.subscription.deleted",
                 "customer.subscription.paused", "customer.subscription.resumed" ->
                    stripe.syncSubscription(((Subscription) object(event)).getId());
            case "invoice.paid" -> stripe.recordInvoice(((Invoice) object(event)).getCustomer(), "approved");
            case "invoice.payment_failed" -> stripe.recordInvoice(((Invoice) object(event)).getCustomer(), "rejected");
            default -> log.debug("Stripe: evento {} ignorado", event.getType());
        }

        processedEvents.save(new BillingWebhookEvent(event.getId(), StripeBillingProvider.ID, event.getType()));
        return ResponseEntity.ok().build();
    }

    /** Objeto del evento aunque la versión de API de la cuenta difiera de la del SDK (solo se leen ids). */
    private static StripeObject object(Event event) {
        var deserializer = event.getDataObjectDeserializer();
        return deserializer.getObject().orElseGet(() -> {
            try {
                return deserializer.deserializeUnsafe();
            } catch (Exception ex) {
                throw new IllegalStateException("No se pudo leer el evento " + event.getId(), ex);
            }
        });
    }
}
