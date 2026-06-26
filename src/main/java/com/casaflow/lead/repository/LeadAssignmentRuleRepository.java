package com.casaflow.lead.repository;

import com.casaflow.lead.domain.LeadAssignmentRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadAssignmentRuleRepository extends JpaRepository<LeadAssignmentRule, UUID> {
    List<LeadAssignmentRule> findByCompanyIdAndDeletedAtIsNullOrderByPriorityAsc(UUID companyId);

    @Query("SELECT r FROM LeadAssignmentRule r WHERE r.companyId = :companyId " +
           "AND r.active = true AND r.deletedAtIsNull ORDER BY r.priority ASC")
    List<LeadAssignmentRule> findActiveRules(UUID companyId);

    Optional<LeadAssignmentRule> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
