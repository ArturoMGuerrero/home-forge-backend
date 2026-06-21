package com.casaflow.match.repository;

import com.casaflow.match.domain.LeadPropertyMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadPropertyMatchRepository extends JpaRepository<LeadPropertyMatch, UUID> {
    List<LeadPropertyMatch> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);
    Optional<LeadPropertyMatch> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
    boolean existsByCompanyIdAndLeadIdAndPropertyIdAndDeletedAtIsNull(UUID companyId, UUID leadId, UUID propertyId);
}
