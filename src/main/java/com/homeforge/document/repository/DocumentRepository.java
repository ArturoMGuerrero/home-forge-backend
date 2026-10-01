package com.homeforge.document.repository;

import com.homeforge.document.domain.Document;
import com.homeforge.document.domain.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);

    List<Document> findByLeadIdAndCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID leadId, UUID companyId);

    List<Document> findByPropertyIdAndCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID propertyId, UUID companyId);

    List<Document> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, DocumentStatus status);

    Optional<Document> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
