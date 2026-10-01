package com.homeforge.billing.stripe;

import com.homeforge.billing.BillingException;
import com.homeforge.billing.BillingProvider;
import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.subscription.PlanCode;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Component
public class StripeBillingProvider implements BillingProvider {

    public static final String ID = "stripe";
    private static final Logger log = LoggerFactory.getLogger(StripeBillingProvider.class);
    /** Stripe exige que el fin de una prueba esté al menos 48 h en el futuro. */
    private static final Duration MIN_TRIAL_REMAINING = Duration.ofHours(49);

    private final StripeClient client;
    private final StripeCatalog catalog;
    private final CompanyRepository companyRepository;
    private final String frontendUrl;

    public StripeBillingProvider(
            CompanyRepository companyRepository,
            @Value("${stripe.secret-key:}") String secretKey,
            @Value("${app.frontend-url:http://localhost:5174}") String frontendUrl,
            @Value("${stripe.plan.starter.price:299}") long starterPrice,
            @Value("${stripe.plan.pro.price:999}") long proPrice,
            @Value("${stripe.plan.business.price:3999}") long businessPrice
    ) {
        this.companyRepository = companyRepository;
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
        this.client = secretKey.isBlank() ? null : new StripeClient(secretKey);
        Map<PlanCode, Long> prices = new EnumMap<>(PlanCode.class);
        prices.put(PlanCode.STARTER, starterPrice);
        prices.put(PlanCode.PRO, proPrice);
        prices.put(PlanCode.BUSINESS, businessPrice);
        this.catalog = client == null ? null : new StripeCatalog(client, prices);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean isConfigured() {
        return client != null;
    }

    StripeClient client() {
        requireConfigured();
        return client;
    }

    @Override
    @Transactional
    public String startCheckout(Company company, String payerEmail, PlanCode plan) {
        requireConfigured();
        try {
            SessionCreateParams.SubscriptionData.Builder subscription = SessionCreateParams.SubscriptionData.builder()
                    .putMetadata("companyId", company.getId().toString());
            // Respeta los días de prueba que le quedan: el primer cobro se hace al terminar la prueba.
            Instant trialEndsAt = company.getTrialEndsAt();
            if ("TRIAL".equals(company.getSubscriptionStatus()) && trialEndsAt != null
                    && trialEndsAt.isAfter(Instant.now().plus(MIN_TRIAL_REMAINING))) {
                subscription.setTrialEnd(trialEndsAt.getEpochSecond());
            }
            return client.v1().checkout().sessions().create(SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(customerFor(company, payerEmail))
                    .setClientReferenceId(company.getId().toString())
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(catalog.priceId(plan))
                            .setQuantity(1L)
                            .build())
                    .setSubscriptionData(subscription.build())
                    .setLocale(SessionCreateParams.Locale.ES_419)
                    .setAllowPromotionCodes(true)
                    .setSuccessUrl(frontendUrl + "/app/planes?checkout=success")
                    .setCancelUrl(frontendUrl + "/app/planes?checkout=cancel")
                    .build()).getUrl();
        } catch (StripeException ex) {
            log.error("Stripe: no se pudo crear el checkout para la empresa {}", company.getId(), ex);
            throw new BillingException("No se pudo iniciar el pago. Intenta de nuevo en unos minutos.", ex);
        }
    }

    @Override
    public String managementUrl(Company company) {
        requireConfigured();
        if (company.getStripeCustomerId() == null) {
            throw new BillingException("Todavía no tienes una suscripción para administrar.");
        }
        try {
            // Durante la prueba el portal no ofrece cambio de plan: ahí cobraría de inmediato.
            String configuration = trialingSubscription(company) != null
                    ? catalog.trialPortalConfigurationId()
                    : catalog.portalConfigurationId();
            return client.v1().billingPortal().sessions().create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(company.getStripeCustomerId())
                            .setConfiguration(configuration)
                            .setLocale(com.stripe.param.billingportal.SessionCreateParams.Locale.ES_419)
                            .setReturnUrl(frontendUrl + "/app/planes")
                            .build()).getUrl();
        } catch (StripeException ex) {
            log.error("Stripe: no se pudo abrir el portal para la empresa {}", company.getId(), ex);
            throw new BillingException("No se pudo abrir el portal de pagos. Intenta de nuevo en unos minutos.", ex);
        }
    }

    @Override
    @Transactional
    public boolean changePlanDuringTrial(Company company, PlanCode plan) {
        requireConfigured();
        try {
            Subscription subscription = trialingSubscription(company);
            if (subscription == null) {
                return false;
            }
            SubscriptionItem item = subscription.getItems().getData().getFirst();
            String priceId = catalog.priceId(plan);
            if (!priceId.equals(item.getPrice().getId())) {
                // Sin prorrateo y sin tocar trial_end: la prueba sigue y no se genera ningún cargo ahora.
                client.v1().subscriptions().update(subscription.getId(), SubscriptionUpdateParams.builder()
                        .addItem(SubscriptionUpdateParams.Item.builder().setId(item.getId()).setPrice(priceId).build())
                        .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.NONE)
                        .build());
            }
            syncSubscription(subscription.getId());
            return true;
        } catch (StripeException ex) {
            log.error("Stripe: no se pudo cambiar el plan en prueba de la empresa {}", company.getId(), ex);
            throw new BillingException("No se pudo cambiar el plan. Intenta de nuevo en unos minutos.", ex);
        }
    }

    /** La suscripción de la empresa si está en periodo de prueba en Stripe; null en cualquier otro caso. */
    private Subscription trialingSubscription(Company company) throws StripeException {
        if (company.getStripeSubscriptionId() == null) {
            return null;
        }
        Subscription subscription = client.v1().subscriptions().retrieve(company.getStripeSubscriptionId());
        return "trialing".equals(subscription.getStatus()) ? subscription : null;
    }

    /**
     * Copia a la empresa el estado actual de la suscripción en Stripe. Siempre se consulta la versión
     * más reciente a Stripe, así no importa si los webhooks llegan repetidos o fuera de orden.
     */
    @Transactional
    public void syncSubscription(String subscriptionId) {
        try {
            Subscription subscription = client().v1().subscriptions().retrieve(subscriptionId);
            Company company = companyRepository.findByStripeCustomerId(subscription.getCustomer())
                    .or(() -> companyFromMetadata(subscription))
                    .orElse(null);
            if (company == null) {
                log.warn("Stripe: la suscripción {} no corresponde a ninguna empresa", subscriptionId);
                return;
            }
            if (company.getStripeSubscriptionId() != null && !company.getStripeSubscriptionId().equals(subscriptionId)
                    && "canceled".equals(subscription.getStatus())) {
                // Una suscripción vieja que se canceló no debe pisar a la vigente.
                return;
            }
            SubscriptionItem item = subscription.getItems().getData().getFirst();
            PlanCode plan = StripeCatalog.planForLookupKey(item.getPrice().getLookupKey()).orElse(null);
            Instant periodEnd = item.getCurrentPeriodEnd() == null ? null : Instant.ofEpochSecond(item.getCurrentPeriodEnd());
            // Las versiones recientes de la API programan la cancelación con cancel_at en lugar de cancel_at_period_end.
            boolean cancelScheduled = Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd()) || subscription.getCancelAt() != null;
            if (subscription.getCancelAt() != null) {
                periodEnd = Instant.ofEpochSecond(subscription.getCancelAt());
            }
            company.applyBillingSubscription(ID, subscription.getId(), plan, toStatus(subscription.getStatus()),
                    periodEnd, cancelScheduled);
            companyRepository.save(company);
            log.info("Stripe: empresa {} -> plan {} ({})", company.getId(), company.getPlanCode(), subscription.getStatus());
        } catch (StripeException ex) {
            throw new BillingException("No se pudo consultar la suscripción en Stripe", ex);
        }
    }

    @Transactional
    public void recordInvoice(String customerId, String paymentStatus) {
        companyRepository.findByStripeCustomerId(customerId).ifPresent(company -> {
            company.recordPayment(ID, paymentStatus);
            companyRepository.save(company);
        });
    }

    @Transactional
    public void linkCustomer(String companyId, String customerId) {
        companyRepository.findById(java.util.UUID.fromString(companyId)).ifPresent(company -> {
            if (company.getStripeCustomerId() == null) {
                company.linkStripeCustomer(customerId);
                companyRepository.save(company);
            }
        });
    }

    /** Estado de la suscripción en HomeForge según el estado en Stripe. */
    static String toStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "active", "trialing" -> "ACTIVE";
            case "past_due", "incomplete" -> "PENDING";
            case "canceled" -> "CANCELLED";
            default -> "SUSPENDED"; // unpaid, incomplete_expired, paused
        };
    }

    private String customerFor(Company company, String payerEmail) throws StripeException {
        if (company.getStripeCustomerId() != null) {
            return company.getStripeCustomerId();
        }
        Customer customer = client.v1().customers().create(CustomerCreateParams.builder()
                        .setEmail(payerEmail)
                        .setName(company.getName())
                        .putMetadata("companyId", company.getId().toString())
                        .build(),
                RequestOptions.builder().setIdempotencyKey("homeforge-customer-" + company.getId()).build());
        company.linkStripeCustomer(customer.getId());
        companyRepository.save(company);
        return customer.getId();
    }

    private java.util.Optional<Company> companyFromMetadata(Subscription subscription) {
        String companyId = subscription.getMetadata() == null ? null : subscription.getMetadata().get("companyId");
        if (companyId == null) {
            return java.util.Optional.empty();
        }
        return companyRepository.findById(java.util.UUID.fromString(companyId)).map(company -> {
            if (company.getStripeCustomerId() == null) {
                company.linkStripeCustomer(subscription.getCustomer());
            }
            return company;
        });
    }

    private void requireConfigured() {
        if (client == null) {
            throw new BillingException("Los pagos con Stripe no están configurados en este entorno.");
        }
    }
}
