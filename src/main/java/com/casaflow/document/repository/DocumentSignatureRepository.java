package com.casaflow.document.repository;

import com.casaflow.document.domain.DocumentSignature;
import com.casaflow.document.domain.SignatureStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentSignatureRepository extends JpaRepository<DocumentSignature, UUID> {
    List<DocumentSignature> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);

    List<DocumentSignature> findByDocumentIdAndStatusOrderByCreatedAtAsc(UUID documentId, SignatureStatus status);

    Optional<DocumentSignature> findByIdAndCompanyId(UUID id, UUID companyId);

    long countByDocumentIdAndStatus(UUID documentId, SignatureStatus status);
}
