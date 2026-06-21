package com.casaflow.lead.repository;

import com.casaflow.lead.domain.LeadActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, UUID> {
    List<LeadActivity> findByLeadIdAndCompanyIdOrderByOccurredAtDesc(UUID leadId, UUID companyId);
}
