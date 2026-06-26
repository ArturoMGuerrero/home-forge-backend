package com.casaflow.document.repository;

import com.casaflow.document.domain.DocumentTemplate;
import com.casaflow.document.domain.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {
    List<DocumentTemplate> findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(UUID companyId);

    List<DocumentTemplate> findByCompanyIdAndActiveAndDeletedAtIsNullOrderByNameAsc(UUID companyId, boolean active);

    List<DocumentTemplate> findByCompanyIdAndDocumentTypeAndDeletedAtIsNull(UUID companyId, DocumentType documentType);

    Optional<DocumentTemplate> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);

    Optional<DocumentTemplate> findByCompanyIdAndIsDefaultAndDocumentTypeAndDeletedAtIsNull(
        UUID companyId,
        boolean isDefault,
        DocumentType documentType
    );
}
