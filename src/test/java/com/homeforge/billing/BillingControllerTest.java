package com.homeforge.billing;

import com.homeforge.billing.stripe.StripeBillingProvider;
import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.security.CurrentUser;
import com.homeforge.subscription.PlanCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillingControllerTest {

    /** Proveedor falso: la suscripción está en prueba según {@code trialing}. */
    static final class FakeProvider implements BillingProvider {
        boolean trialing;
        PlanCode changedTo;

        @Override public String id() { return StripeBillingProvider.ID; }
        @Override public boolean isConfigured() { return true; }
        @Override public String startCheckout(Company company, String payerEmail, PlanCode plan) { return "https://pago"; }
        @Override public String managementUrl(Company company) { return "https://portal"; }

        @Override
        public boolean changePlanDuringTrial(Company company, PlanCode plan) {
            if (!trialing) return false;
            changedTo = plan;
            return true;
        }
    }

    private final UUID companyId = UUID.randomUUID();
    private final CompanyRepository companies = mock(CompanyRepository.class);
    private final FakeProvider provider = new FakeProvider();
    private final BillingController controller = new BillingController(List.of(provider), companies);
    private final Company company = new Company("Inmobiliaria", "MX", "QRO", "MXN", "America/Mexico_City");

    @BeforeEach
    void signIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(new CurrentUser(UUID.randomUUID(), companyId, "admin@example.com", "ADMIN"), null));
        when(companies.findById(companyId)).thenReturn(Optional.of(company));
    }

    @AfterEach
    void signOut() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void duringTheTrialChangesThePlanWithoutLeavingHomeForge() {
        company.applyBillingSubscription("stripe", "sub_1", PlanCode.STARTER, "ACTIVE", null, false);
        provider.trialing = true;

        BillingController.CheckoutResponse response = controller.checkout(new BillingController.CheckoutRequest(PlanCode.PRO));

        assertTrue(response.planChanged());
        assertNull(response.url());
        assertEquals(PlanCode.PRO, provider.changedTo);
    }

    @Test
    void afterTheTrialSendsToThePortal() {
        company.applyBillingSubscription("stripe", "sub_1", PlanCode.STARTER, "ACTIVE", null, false);

        BillingController.CheckoutResponse response = controller.checkout(new BillingController.CheckoutRequest(PlanCode.PRO));

        assertFalse(response.planChanged());
        assertEquals("https://portal", response.url());
    }

    @Test
    void withoutSubscriptionStartsCheckout() {
        BillingController.CheckoutResponse response = controller.checkout(new BillingController.CheckoutRequest(PlanCode.PRO));

        assertFalse(response.planChanged());
        assertEquals("https://pago", response.url());
    }
}
