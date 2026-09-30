package com.casaflow.lead.repository;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.domain.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.*;
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    List<Lead> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);
    Optional<Lead> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
    List<Lead> findByStatusAndCreatedAtBeforeAndDeletedAtIsNull(LeadStatus status, Instant createdBefore);
}
