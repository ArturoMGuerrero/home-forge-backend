package com.casaflow.lead.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lead_assignment_rules")
public class LeadAssignmentRule extends AuditableEntity {
    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int priority = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssignmentStrategy assignmentStrategy = AssignmentStrategy.ROUND_ROBIN;

    private String criteriaSource;
    private String criteriaListingType;
    private String criteriaCity;
    private BigDecimal criteriaBudgetMin;
    private BigDecimal criteriaBudgetMax;
    private String criteriaPropertyType;

    @Column(columnDefinition = "text")
    private String assignedUserIds;

    protected LeadAssignmentRule() {}

    public LeadAssignmentRule(
        UUID companyId,
        String name,
        String description,
        int priority,
        AssignmentStrategy assignmentStrategy
    ) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.assignmentStrategy = assignmentStrategy;
    }

    public UUID getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public int getPriority() { return priority; }
    public AssignmentStrategy getAssignmentStrategy() { return assignmentStrategy; }
    public String getCriteriaSource() { return criteriaSource; }
    public String getCriteriaListingType() { return criteriaListingType; }
    public String getCriteriaCity() { return criteriaCity; }
    public BigDecimal getCriteriaBudgetMin() { return criteriaBudgetMin; }
    public BigDecimal getCriteriaBudgetMax() { return criteriaBudgetMax; }
    public String getCriteriaPropertyType() { return criteriaPropertyType; }
    public String getAssignedUserIds() { return assignedUserIds; }

    public void setActive(boolean active) { this.active = active; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCriteriaSource(String criteriaSource) { this.criteriaSource = criteriaSource; }
    public void setCriteriaListingType(String criteriaListingType) { this.criteriaListingType = criteriaListingType; }
    public void setCriteriaCity(String criteriaCity) { this.criteriaCity = criteriaCity; }
    public void setCriteriaBudgetMin(BigDecimal criteriaBudgetMin) { this.criteriaBudgetMin = criteriaBudgetMin; }
    public void setCriteriaBudgetMax(BigDecimal criteriaBudgetMax) { this.criteriaBudgetMax = criteriaBudgetMax; }
    public void setCriteriaPropertyType(String criteriaPropertyType) { this.criteriaPropertyType = criteriaPropertyType; }
    public void setAssignedUserIds(String assignedUserIds) { this.assignedUserIds = assignedUserIds; }
}
