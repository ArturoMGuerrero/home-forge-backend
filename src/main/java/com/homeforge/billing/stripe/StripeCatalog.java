package com.homeforge.billing.stripe;

import com.homeforge.billing.BillingException;
import com.homeforge.subscription.PlanCode;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.billingportal.Configuration;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.billingportal.ConfigurationCreateParams;
import com.stripe.param.billingportal.ConfigurationListParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Productos, precios y configuración del portal de HomeForge en Stripe. Se crean solos la primera vez
 * (identificados por lookup key y metadata), así un entorno nuevo no requiere configurar nada en el panel.
 */
class StripeCatalog {

    private static final Logger log = LoggerFactory.getLogger(StripeCatalog.class);
    private static final String APP = "homeforge";
    private static final String CURRENCY = "mxn";
    private static final String TRIAL_VARIANT = "trial";

    private final StripeClient client;
    private final Map<PlanCode, Long> monthlyPricesMxn;
    private volatile Map<PlanCode, Price> prices;
    private volatile String portalConfigurationId;
    private volatile String trialPortalConfigurationId;

    StripeCatalog(StripeClient client, Map<PlanCode, Long> monthlyPricesMxn) {
        this.client = client;
        this.monthlyPricesMxn = monthlyPricesMxn;
    }

    static String lookupKey(PlanCode plan) {
        return APP + "_" + plan.name().toLowerCase(Locale.ROOT) + "_monthly";
    }

    /** Plan al que pertenece un precio de Stripe, según su lookup key. */
    static Optional<PlanCode> planForLookupKey(String lookupKey) {
        for (PlanCode plan : PlanCode.values()) {
            if (lookupKey(plan).equals(lookupKey)) {
                return Optional.of(plan);
            }
        }
        return Optional.empty();
    }

    String priceId(PlanCode plan) {
        return prices().get(plan).getId();
    }

    /** Portal normal: tarjeta, facturas, cambio de plan (con prorrateo) y cancelación. */
    synchronized String portalConfigurationId() {
        if (portalConfigurationId == null) {
            portalConfigurationId = findOrCreatePortal(false);
        }
        return portalConfigurationId;
    }

    /**
     * Portal para suscripciones en prueba: igual al normal pero sin cambio de plan. En el portal, cambiar de
     * plan termina la prueba y cobra ese día; durante la prueba el cambio se hace desde HomeForge sin cobro.
     */
    synchronized String trialPortalConfigurationId() {
        if (trialPortalConfigurationId == null) {
            trialPortalConfigurationId = findOrCreatePortal(true);
        }
        return trialPortalConfigurationId;
    }

    private String findOrCreatePortal(boolean trial) {
        String variant = trial ? TRIAL_VARIANT : null;
        try {
            for (Configuration existing : client.v1().billingPortal().configurations()
                    .list(ConfigurationListParams.builder().setActive(true).setLimit(100L).build()).getData()) {
                if (APP.equals(existing.getMetadata().get("app")) && Objects.equals(variant, existing.getMetadata().get("variant"))) {
                    return existing.getId();
                }
            }
            ConfigurationCreateParams.Features.Builder features = ConfigurationCreateParams.Features.builder()
                    .setInvoiceHistory(ConfigurationCreateParams.Features.InvoiceHistory.builder().setEnabled(true).build())
                    .setPaymentMethodUpdate(ConfigurationCreateParams.Features.PaymentMethodUpdate.builder().setEnabled(true).build())
                    .setSubscriptionCancel(ConfigurationCreateParams.Features.SubscriptionCancel.builder()
                            .setEnabled(true)
                            .setMode(ConfigurationCreateParams.Features.SubscriptionCancel.Mode.AT_PERIOD_END)
                            .build());
            if (!trial) {
                ConfigurationCreateParams.Features.SubscriptionUpdate.Builder update =
                        ConfigurationCreateParams.Features.SubscriptionUpdate.builder()
                                .setEnabled(true)
                                .addDefaultAllowedUpdate(ConfigurationCreateParams.Features.SubscriptionUpdate.DefaultAllowedUpdate.PRICE)
                                .setProrationBehavior(ConfigurationCreateParams.Features.SubscriptionUpdate.ProrationBehavior.CREATE_PRORATIONS);
                for (Price price : prices().values()) {
                    update.addProduct(ConfigurationCreateParams.Features.SubscriptionUpdate.Product.builder()
                            .setProduct(price.getProduct())
                            .addPrice(price.getId())
                            .build());
                }
                features.setSubscriptionUpdate(update.build());
            }
            ConfigurationCreateParams.Builder params = ConfigurationCreateParams.builder()
                    .setBusinessProfile(ConfigurationCreateParams.BusinessProfile.builder()
                            .setHeadline("Administra tu suscripción de HomeForge")
                            .build())
                    .setFeatures(features.build())
                    .putMetadata("app", APP);
            if (trial) {
                params.putMetadata("variant", TRIAL_VARIANT);
            }
            Configuration created = client.v1().billingPortal().configurations().create(params.build());
            log.info("Stripe: se creó la configuración del portal de clientes {}{}", created.getId(), trial ? " (prueba)" : "");
            return created.getId();
        } catch (StripeException ex) {
            throw new BillingException("No se pudo preparar el portal de pagos", ex);
        }
    }

    private Map<PlanCode, Price> prices() {
        Map<PlanCode, Price> current = prices;
        if (current == null) {
            synchronized (this) {
                if (prices == null) {
                    prices = loadOrCreatePrices();
                }
                current = prices;
            }
        }
        return current;
    }

    private Map<PlanCode, Price> loadOrCreatePrices() {
        Map<PlanCode, Price> result = new EnumMap<>(PlanCode.class);
        try {
            for (PlanCode plan : PlanCode.values()) {
                long amountCents = monthlyPricesMxn.get(plan) * 100;
                Price existing = client.v1().prices().list(PriceListParams.builder()
                                .addLookupKey(lookupKey(plan))
                                .setActive(true)
                                .build())
                        .getData().stream().findFirst().orElse(null);
                if (existing != null && existing.getUnitAmount() == amountCents && CURRENCY.equals(existing.getCurrency())) {
                    result.put(plan, existing);
                    continue;
                }
                String productId = existing != null ? existing.getProduct() : createProduct(plan).getId();
                // Si el precio configurado cambió, el nuevo precio toma la lookup key; las suscripciones existentes conservan el anterior.
                Price created = client.v1().prices().create(PriceCreateParams.builder()
                        .setProduct(productId)
                        .setCurrency(CURRENCY)
                        .setUnitAmount(amountCents)
                        .setRecurring(PriceCreateParams.Recurring.builder()
                                .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                .build())
                        .setLookupKey(lookupKey(plan))
                        .setTransferLookupKey(true)
                        .putMetadata("app", APP)
                        .putMetadata("plan", plan.name())
                        .build());
                log.info("Stripe: se creó el precio {} para el plan {} ({} MXN/mes)", created.getId(), plan, amountCents / 100);
                result.put(plan, created);
            }
            return result;
        } catch (StripeException ex) {
            throw new BillingException("No se pudieron preparar los planes en Stripe", ex);
        }
    }

    private Product createProduct(PlanCode plan) throws StripeException {
        String name = plan.name().charAt(0) + plan.name().substring(1).toLowerCase(Locale.ROOT);
        return client.v1().products().create(ProductCreateParams.builder()
                .setName("HomeForge " + name)
                .setDescription("Suscripción mensual al plan " + name + " de HomeForge (hasta " + plan.getUserLimit() + " usuarios)")
                .putMetadata("app", APP)
                .putMetadata("plan", plan.name())
                .build());
    }
}
