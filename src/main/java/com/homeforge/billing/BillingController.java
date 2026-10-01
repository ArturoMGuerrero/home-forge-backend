package com.homeforge.billing;

import com.homeforge.billing.stripe.StripeBillingProvider;
import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.security.CurrentUser;
import com.homeforge.subscription.PlanCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/** Contratación y administración de la suscripción de la empresa del usuario (solo administradores). */
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    /** Con estos estados ya hay una suscripción vigente: se administra en el portal en lugar de contratar otra. */
    private static final Set<String> MANAGED_STATUSES = Set.of("ACTIVE", "PENDING");

    private final List<BillingProvider> providers;
    private final CompanyRepository companyRepository;

    public BillingController(List<BillingProvider> providers, CompanyRepository companyRepository) {
        this.providers = providers;
        this.companyRepository = companyRepository;
    }

    public record ProviderInfo(String id, boolean configured) {}

    public record CheckoutRequest(@NotNull PlanCode planCode) {}

    public record RedirectResponse(String url) {}

    /** {@code url}: a dónde llevar al usuario; o {@code planChanged}: el plan ya cambió sin salir de HomeForge. */
    public record CheckoutResponse(String url, boolean planChanged) {}

    @GetMapping("/providers")
    public List<ProviderInfo> providers() {
        return providers.stream().map(p -> new ProviderInfo(p.id(), p.isConfigured())).toList();
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        CurrentUser user = currentUser();
        Company company = company(user);
        BillingProvider provider = stripe();
        if (company.getStripeSubscriptionId() != null && MANAGED_STATUSES.contains(company.getSubscriptionStatus())) {
            if (provider.changePlanDuringTrial(company, request.planCode())) {
                return new CheckoutResponse(null, true);
            }
            return new CheckoutResponse(provider.managementUrl(company), false);
        }
        return new CheckoutResponse(provider.startCheckout(company, user.email(), request.planCode()), false);
    }

    @PostMapping("/portal")
    public RedirectResponse portal() {
        return new RedirectResponse(stripe().managementUrl(company(currentUser())));
    }

    private BillingProvider stripe() {
        return providers.stream()
                .filter(p -> StripeBillingProvider.ID.equals(p.id()))
                .findFirst()
                .orElseThrow(() -> new BillingException("Los pagos no están disponibles."));
    }

    private Company company(CurrentUser user) {
        return companyRepository.findById(user.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }

    private static CurrentUser currentUser() {
        return CurrentUser.get().orElseThrow(() -> new AccessDeniedException("Inicia sesión para continuar"));
    }
}
