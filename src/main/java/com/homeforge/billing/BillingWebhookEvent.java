package com.homeforge.billing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Evento de webhook ya procesado; los proveedores reenvían eventos y cada uno debe aplicarse una sola vez. */
@Entity
@Table(name = "billing_webhook_events")
public class BillingWebhookEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    protected BillingWebhookEvent() {}

    public BillingWebhookEvent(String eventId, String provider, String eventType) {
        this.eventId = eventId;
        this.provider = provider;
        this.eventType = eventType;
    }
}
