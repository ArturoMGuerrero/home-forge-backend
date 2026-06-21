package com.casaflow.company.domain;

import com.casaflow.subscription.PlanCode;
import com.casaflow.shared.audit.AuditableEntity;
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
    public Instant getTrialStartedAt() { return trialStartedAt; }
    public Instant getTrialEndsAt() { return trialEndsAt; }
    public Instant getNextBillingAt() { return nextBillingAt; }

    public void changePlan(PlanCode planCode) {
        this.planCode = planCode;
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
