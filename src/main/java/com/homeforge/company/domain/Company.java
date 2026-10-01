package com.homeforge.company.domain;

import com.homeforge.subscription.PlanCode;
import com.homeforge.shared.audit.AuditableEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity @Table(name = "companies")
public class Company extends AuditableEntity {
    @Column(nullable=false) private String name;
    @Column(nullable=false, length=2) private String countryCode;
    @Column(nullable=false) private String stateCode;
    @Column(nullable=false) private String defaultLanguage = "en";
    @Column(nullable=false, length=3) private String defaultCurrency;
    @Column(nullable=false) private String timezone;
    @Column(length=180) private String publicEmail;
    @Column(name="public_phone_e164", length=20) private String publicPhoneE164;
    @Column(columnDefinition="text") private String publicDescription;
    @Column(columnDefinition="text") private String mission;
    @Column(columnDefinition="text") private String vision;
    @Column(length=255) private String address;
    @Column(length=120) private String city;
    @Column(length=20) private String postalCode;
    @Column(length=500) private String websiteUrl;
    @Column(length=120) private String professionalLicense;
    private Integer yearsExperience;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private PlanCode planCode = PlanCode.STARTER;
    @Column(nullable=false, length=20) private String subscriptionStatus = "TRIAL";
    private Instant trialStartedAt = Instant.now();
    private Instant trialEndsAt = Instant.now().plus(14, ChronoUnit.DAYS);
    private Instant nextBillingAt;
    @Column(name="mercadopago_customer_id", length=100) private String mercadoPagoCustomerId;
    @Column(name="mercadopago_subscription_id", length=100) private String mercadoPagoSubscriptionId;
    @Column(length=50) private String paymentMethod;
    private Instant lastPaymentAt;
    @Column(length=50) private String lastPaymentStatus;
    @Column(name="stripe_customer_id", length=100) private String stripeCustomerId;
    @Column(name="stripe_subscription_id", length=100) private String stripeSubscriptionId;
    @Column(nullable=false) private boolean cancelAtPeriodEnd = false;
    protected Company() {}
    public Company(String name, String countryCode, String stateCode, String defaultCurrency, String timezone) {
        this.name = name; this.countryCode = countryCode; this.stateCode = stateCode; this.defaultCurrency = defaultCurrency; this.timezone = timezone;
    }
    public Company(
            String name,
            String countryCode,
            String stateCode,
            String defaultCurrency,
            String timezone,
            String publicEmail,
            String publicPhoneE164
    ) {
        this(name, countryCode, stateCode, defaultCurrency, timezone);
        this.publicEmail = publicEmail;
        this.publicPhoneE164 = publicPhoneE164;
    }
    public String getName() { return name; }
    public String getCountryCode() { return countryCode; }
    public String getStateCode() { return stateCode; }
    public String getDefaultCurrency() { return defaultCurrency; }
    public String getPublicEmail() { return publicEmail; }
    public String getPublicPhoneE164() { return publicPhoneE164; }
    public String getPublicDescription() { return publicDescription; }
    public String getMission() { return mission; }
    public String getVision() { return vision; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getProfessionalLicense() { return professionalLicense; }
    public Integer getYearsExperience() { return yearsExperience; }
    public PlanCode getPlanCode() { return planCode; }
    public String getSubscriptionStatus() { return subscriptionStatus; }

    /**
     * Calcula el estado real de la suscripción verificando si ha expirado.
     * Si el estado es TRIAL o ACTIVE pero la fecha de expiración ya pasó, devuelve EXPIRED.
     */
    public String getComputedSubscriptionStatus() {
        Instant now = Instant.now();

        // Verificar si el trial ha expirado
        if ("TRIAL".equals(subscriptionStatus) && trialEndsAt != null && trialEndsAt.isBefore(now)) {
            return "EXPIRED";
        }

        // Verificar si la suscripción activa ha expirado
        if ("ACTIVE".equals(subscriptionStatus) && nextBillingAt != null && nextBillingAt.isBefore(now)) {
            return "EXPIRED";
        }

        return subscriptionStatus;
    }

    public Instant getTrialStartedAt() { return trialStartedAt; }
    public Instant getTrialEndsAt() { return trialEndsAt; }
    public Instant getNextBillingAt() { return nextBillingAt; }
    public String getMercadoPagoCustomerId() { return mercadoPagoCustomerId; }
    public String getMercadoPagoSubscriptionId() { return mercadoPagoSubscriptionId; }
    public String getPaymentMethod() { return paymentMethod; }
    public Instant getLastPaymentAt() { return lastPaymentAt; }
    public String getLastPaymentStatus() { return lastPaymentStatus; }
    public String getStripeCustomerId() { return stripeCustomerId; }
    public String getStripeSubscriptionId() { return stripeSubscriptionId; }
    public boolean isCancelAtPeriodEnd() { return cancelAtPeriodEnd; }

    public void linkStripeCustomer(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    /** Refleja en la empresa el estado de su suscripción en el proveedor de pagos (lo dicta el proveedor, no el cliente). */
    public void applyBillingSubscription(String provider, String subscriptionId, PlanCode planCode, String status,
                                         Instant currentPeriodEnd, boolean cancelAtPeriodEnd) {
        this.paymentMethod = provider;
        this.stripeSubscriptionId = subscriptionId;
        if (planCode != null) {
            this.planCode = planCode;
        }
        this.subscriptionStatus = status;
        this.nextBillingAt = currentPeriodEnd;
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    }

    public void recordPayment(String provider, String paymentStatus) {
        updatePaymentInfo(provider, paymentStatus);
    }

    public void setMercadoPagoCustomerId(String mercadoPagoCustomerId) {
        this.mercadoPagoCustomerId = mercadoPagoCustomerId;
    }

    public void setMercadoPagoSubscriptionId(String mercadoPagoSubscriptionId) {
        this.mercadoPagoSubscriptionId = mercadoPagoSubscriptionId;
    }

    public void updatePaymentInfo(String paymentMethod, String paymentStatus) {
        this.paymentMethod = paymentMethod;
        this.lastPaymentStatus = paymentStatus;
        this.lastPaymentAt = Instant.now();
    }

    public void updateSubscriptionStatus(String status, Instant nextBillingAt) {
        this.subscriptionStatus = status;
        this.nextBillingAt = nextBillingAt;
    }

    public void updateProfile(
            String name,
            String countryCode,
            String stateCode,
            String city,
            String address,
            String postalCode,
            String publicEmail,
            String publicPhoneE164,
            String websiteUrl,
            String publicDescription,
            String mission,
            String vision,
            String professionalLicense,
            Integer yearsExperience
    ) {
        this.name = name;
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.city = city;
        this.address = address;
        this.postalCode = postalCode;
        this.publicEmail = publicEmail;
        this.publicPhoneE164 = publicPhoneE164;
        this.websiteUrl = websiteUrl;
        this.publicDescription = publicDescription;
        this.mission = mission;
        this.vision = vision;
        this.professionalLicense = professionalLicense;
        this.yearsExperience = yearsExperience;
    }
}
