package com.casaflow.lead.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="leads")
public class Lead extends AuditableEntity {
    @Column(nullable=false) private UUID companyId;
    @Column(nullable=false) private String firstName;
    private String middleName;
    @Column(nullable=false) private String lastName;
    private String secondLastName;
    private String email;
    @Column(name="phone_e164") private String phoneE164;
    private String source;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LeadStatus status = LeadStatus.NEW;
    private String listingType;
    private BigDecimal budgetMin;
    @Column(name="budget_amount") private BigDecimal budgetMax;
    @Column(length=3) private String currencyCode;
    @Column(length=2) private String countryCode;
    private String stateCode;
    private String city;
    private String propertyType;
    private Integer bedroomsMin;
    private BigDecimal bathroomsMin;
    private String financingType;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LeadPriority priority = LeadPriority.MEDIUM;
    private String assignedTo;
    private Instant nextFollowUpAt;
    @Column(columnDefinition="text") private String notes;
    @Column(nullable=false) private int score = 0;
    private Instant scoreUpdatedAt;
    protected Lead() {}
    public Lead(
            UUID companyId, String firstName, String lastName, String email, String phoneE164,
            String listingType, BigDecimal budgetMax, String currencyCode, String city
    ) {
        this.companyId=companyId; this.firstName=firstName; this.lastName=lastName; this.email=email; this.phoneE164=phoneE164;
        this.listingType=listingType; this.budgetMax=budgetMax; this.currencyCode=currencyCode; this.city=city;
    }
    public UUID getCompanyId(){return companyId;} public String getFirstName(){return firstName;} public String getLastName(){return lastName;} public String getEmail(){return email;} public String getPhoneE164(){return phoneE164;} public LeadStatus getStatus(){return status;}
    public String getSource(){return source;}
    public String getListingType(){return listingType;}
    public BigDecimal getBudgetMin(){return budgetMin;}
    public BigDecimal getBudgetMax(){return budgetMax;}
    public String getCurrencyCode(){return currencyCode;}
    public String getCountryCode(){return countryCode;}
    public String getStateCode(){return stateCode;}
    public String getCity(){return city;}
    public String getPropertyType(){return propertyType;}
    public Integer getBedroomsMin(){return bedroomsMin;}
    public BigDecimal getBathroomsMin(){return bathroomsMin;}
    public String getFinancingType(){return financingType;}
    public LeadPriority getPriority(){return priority;}
    public String getAssignedTo(){return assignedTo;}
    public Instant getNextFollowUpAt(){return nextFollowUpAt;}
    public String getNotes(){return notes;}
    public int getScore(){return score;}
    public Instant getScoreUpdatedAt(){return scoreUpdatedAt;}
    public void setNextFollowUpAt(Instant nextFollowUpAt){this.nextFollowUpAt=nextFollowUpAt;}
    public void setStatus(LeadStatus status){this.status=status;}
    public void setScore(int score){this.score=score; this.scoreUpdatedAt=Instant.now();}
    public void setAssignedTo(String assignedTo){this.assignedTo=assignedTo;}
    public void update(
            String firstName, String lastName, String email, String phoneE164, String source,
            LeadStatus status, String listingType, BigDecimal budgetMin, BigDecimal budgetMax,
            String currencyCode, String countryCode, String stateCode, String city,
            String propertyType, Integer bedroomsMin, BigDecimal bathroomsMin,
            String financingType, LeadPriority priority, String assignedTo,
            Instant nextFollowUpAt, String notes
    ){
        this.firstName=firstName; this.lastName=lastName; this.email=email; this.phoneE164=phoneE164;
        this.source=source; this.status=status; this.listingType=listingType; this.budgetMin=budgetMin;
        this.budgetMax=budgetMax; this.currencyCode=currencyCode; this.countryCode=countryCode;
        this.stateCode=stateCode; this.city=city; this.propertyType=propertyType;
        this.bedroomsMin=bedroomsMin; this.bathroomsMin=bathroomsMin; this.financingType=financingType;
        this.priority=priority; this.assignedTo=assignedTo; this.nextFollowUpAt=nextFollowUpAt; this.notes=notes;
    }
}
