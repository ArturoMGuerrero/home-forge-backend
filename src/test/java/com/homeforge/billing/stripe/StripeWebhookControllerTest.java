package com.homeforge.billing.stripe;

import com.homeforge.billing.BillingWebhookEvent;
import com.homeforge.billing.BillingWebhookEventRepository;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.subscription.PlanCode;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StripeWebhookControllerTest {

    private static final String SECRET = "whsec_prueba_local";
    private static final String PAYLOAD = """
            {"id":"evt_test_1","object":"event","type":"customer.created","api_version":"2025-01-01",
             "data":{"object":{"id":"cus_test_1","object":"customer"}}}
            """;

    private BillingWebhookEventRepository events;
    private StripeWebhookController controller;

    @BeforeEach
    void setUp() {
        events = mock(BillingWebhookEventRepository.class);
        StripeBillingProvider stripe = new StripeBillingProvider(
                mock(CompanyRepository.class), "sk_test_sin_red", "http://localhost:5174", 299, 999, 3999);
        controller = new StripeWebhookController(stripe, events, SECRET);
    }

    @Test
    void rejectsForgedSignature() {
        assertEquals(400, controller.receive(PAYLOAD, sign(PAYLOAD, "whsec_otro_secreto")).getStatusCode().value());
        assertEquals(400, controller.receive(PAYLOAD, null).getStatusCode().value());
        verify(events, never()).save(any());
    }

    @Test
    void acceptsSignedEventAndRecordsIt() {
        when(events.existsById("evt_test_1")).thenReturn(false);
        assertEquals(200, controller.receive(PAYLOAD, sign(PAYLOAD, SECRET)).getStatusCode().value());
        verify(events).save(any(BillingWebhookEvent.class));
    }

    @Test
    void ignoresRepeatedEvent() {
        when(events.existsById("evt_test_1")).thenReturn(true);
        assertEquals(200, controller.receive(PAYLOAD, sign(PAYLOAD, SECRET)).getStatusCode().value());
        verify(events, never()).save(any());
    }

    @Test
    void refusesWebhooksWhenSecretIsMissing() {
        StripeWebhookController unconfigured = new StripeWebhookController(
                new StripeBillingProvider(mock(CompanyRepository.class), "sk_test_sin_red", "http://localhost:5174", 299, 999, 3999),
                events, "");
        assertEquals(503, unconfigured.receive(PAYLOAD, sign(PAYLOAD, SECRET)).getStatusCode().value());
    }

    @Test
    void mapsStripeStatusesAndPlans() {
        assertEquals("ACTIVE", StripeBillingProvider.toStatus("active"));
        assertEquals("ACTIVE", StripeBillingProvider.toStatus("trialing"));
        assertEquals("PENDING", StripeBillingProvider.toStatus("past_due"));
        assertEquals("CANCELLED", StripeBillingProvider.toStatus("canceled"));
        assertEquals("SUSPENDED", StripeBillingProvider.toStatus("unpaid"));
        assertEquals(Optional.of(PlanCode.PRO), StripeCatalog.planForLookupKey("homeforge_pro_monthly"));
        assertEquals(Optional.empty(), StripeCatalog.planForLookupKey("otro_precio"));
    }

    private static String sign(String payload, String secret) {
        long timestamp = Instant.now().getEpochSecond();
        try {
            String signature = Webhook.Util.computeHmacSha256(secret, timestamp + "." + payload);
            return "t=" + timestamp + ",v1=" + signature;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
