package com.homeforge.document.repository;

import com.homeforge.document.domain.StoredDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoredDocumentRepository extends JpaRepository<StoredDocument, UUID> {
    List<StoredDocument> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);
    Optional<StoredDocument> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
