package com.casaflow.lead.repository;
import com.casaflow.lead.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    List<Lead> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);
    Optional<Lead> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
