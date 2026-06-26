package com.casaflow.lead.repository;

import com.casaflow.lead.domain.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, UUID> {
    List<LeadActivity> findByLeadIdAndCompanyIdOrderByOccurredAtDesc(UUID leadId, UUID companyId);
    Optional<LeadActivity> findByIdAndLeadIdAndCompanyId(UUID activityId, UUID leadId, UUID companyId);
    List<LeadActivity> findByUserIdAndCompanyIdOrderByOccurredAtDesc(UUID userId, UUID companyId);
    List<LeadActivity> findByPropertyIdAndCompanyIdOrderByOccurredAtDesc(UUID propertyId, UUID companyId);
}
