package com.casaflow.document.service;

import com.casaflow.document.domain.DocumentSignature;
import com.casaflow.document.domain.SignatureStatus;
import com.casaflow.document.dto.CreateSignatureRequest;
import com.casaflow.document.repository.DocumentSignatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentSignatureService {
    private final DocumentSignatureRepository repository;
    private final DocumentGenerationService documentService;

    public DocumentSignatureService(
        DocumentSignatureRepository repository,
        DocumentGenerationService documentService
    ) {
        this.repository = repository;
        this.documentService = documentService;
    }

    @Transactional
    public DocumentSignature create(CreateSignatureRequest r) {
        DocumentSignature signature = new DocumentSignature(
            r.companyId(),
            r.documentId(),
            r.signerName(),
            r.signerEmail(),
            r.signerRole()
        );

        signature.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));

        return repository.save(signature);
    }

    public List<DocumentSignature> listByDocument(UUID documentId) {
        return repository.findByDocumentIdOrderByCreatedAtAsc(documentId);
    }

    public List<DocumentSignature> listPending(UUID documentId) {
        return repository.findByDocumentIdAndStatusOrderByCreatedAtAsc(documentId, SignatureStatus.PENDING);
    }

    @Transactional
    public DocumentSignature sign(
        UUID signatureId,
        UUID companyId,
        String signatureData,
        String ipAddress,
        String userAgent
    ) {
        DocumentSignature signature = repository.findByIdAndCompanyId(signatureId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Firma no encontrada"));

        if (signature.getStatus() == SignatureStatus.SIGNED) {
            throw new IllegalArgumentException("Este documento ya ha sido firmado");
        }

        if (signature.getExpiresAt() != null && signature.getExpiresAt().isBefore(Instant.now())) {
            signature.setStatus(SignatureStatus.EXPIRED);
            repository.save(signature);
            throw new IllegalArgumentException("La solicitud de firma ha expirado");
        }

        signature.setSignatureData(signatureData);
        signature.setIpAddress(ipAddress);
        signature.setUserAgent(userAgent);
        signature.setSignedAt(Instant.now());
        signature.setStatus(SignatureStatus.SIGNED);

        DocumentSignature saved = repository.save(signature);

        documentService.checkSignatureCompletion(signature.getDocumentId());

        return saved;
    }

    @Transactional
    public DocumentSignature markAsSent(UUID signatureId, UUID companyId) {
        DocumentSignature signature = repository.findByIdAndCompanyId(signatureId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Firma no encontrada"));

        signature.setStatus(SignatureStatus.SENT);
        signature.setSentAt(Instant.now());

        return repository.save(signature);
    }

    @Transactional
    public DocumentSignature markAsViewed(UUID signatureId, UUID companyId) {
        DocumentSignature signature = repository.findByIdAndCompanyId(signatureId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Firma no encontrada"));

        if (signature.getStatus() == SignatureStatus.SENT) {
            signature.setStatus(SignatureStatus.VIEWED);
        }

        return repository.save(signature);
    }

    @Transactional
    public void delete(UUID signatureId, UUID companyId) {
        DocumentSignature signature = repository.findByIdAndCompanyId(signatureId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Firma no encontrada"));

        if (signature.getStatus() == SignatureStatus.SIGNED) {
            throw new IllegalArgumentException("No se puede eliminar una firma completada");
        }

        repository.delete(signature);
    }
}
