package com.casaflow.lead.service;

import com.casaflow.lead.domain.LeadAssignmentRule;
import com.casaflow.lead.dto.CreateAssignmentRuleRequest;
import com.casaflow.lead.dto.UpdateAssignmentRuleRequest;
import com.casaflow.lead.repository.LeadAssignmentRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentRuleService {
    private final LeadAssignmentRuleRepository repository;

    public AssignmentRuleService(LeadAssignmentRuleRepository repository) {
        this.repository = repository;
    }

    public LeadAssignmentRule create(CreateAssignmentRuleRequest r) {
        LeadAssignmentRule rule = new LeadAssignmentRule(
            r.companyId(),
            r.name(),
            r.description(),
            r.priority(),
            r.assignmentStrategy()
        );

        rule.setCriteriaSource(r.criteriaSource());
        rule.setCriteriaListingType(r.criteriaListingType());
        rule.setCriteriaCity(r.criteriaCity());
        rule.setCriteriaBudgetMin(r.criteriaBudgetMin());
        rule.setCriteriaBudgetMax(r.criteriaBudgetMax());
        rule.setCriteriaPropertyType(r.criteriaPropertyType());
        rule.setAssignedUserIds(r.assignedUserIds());

        return repository.save(rule);
    }

    public List<LeadAssignmentRule> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByPriorityAsc(companyId);
    }

    @Transactional
    public LeadAssignmentRule update(UUID ruleId, UpdateAssignmentRuleRequest r) {
        LeadAssignmentRule rule = repository.findByIdAndCompanyIdAndDeletedAtIsNull(ruleId, r.companyId())
            .orElseThrow(() -> new IllegalArgumentException("Regla no encontrada"));

        if (r.active() != null) {
            rule.setActive(r.active());
        }

        if (r.priority() != null) {
            rule.setPriority(r.priority());
        }

        return repository.save(rule);
    }

    @Transactional
    public void delete(UUID ruleId, UUID companyId) {
        LeadAssignmentRule rule = repository.findByIdAndCompanyIdAndDeletedAtIsNull(ruleId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Regla no encontrada"));
        rule.softDelete();
        repository.save(rule);
    }
}
