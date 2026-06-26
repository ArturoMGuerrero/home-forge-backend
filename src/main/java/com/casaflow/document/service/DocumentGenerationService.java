package com.casaflow.document.service;

import com.casaflow.document.domain.*;
import com.casaflow.document.dto.CreateDocumentRequest;
import com.casaflow.document.repository.DocumentRepository;
import com.casaflow.document.repository.DocumentSignatureRepository;
import com.casaflow.document.repository.DocumentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentGenerationService {
    private final DocumentRepository repository;
    private final DocumentTemplateRepository templateRepository;
    private final DocumentSignatureRepository signatureRepository;

    public DocumentGenerationService(
        DocumentRepository repository,
        DocumentTemplateRepository templateRepository,
        DocumentSignatureRepository signatureRepository
    ) {
        this.repository = repository;
        this.templateRepository = templateRepository;
        this.signatureRepository = signatureRepository;
    }

    @Transactional
    public Document create(CreateDocumentRequest r) {
        Document document = new Document(
            r.companyId(),
            r.name(),
            r.documentType(),
            r.createdByUserId()
        );

        if (r.templateId() != null) {
            DocumentTemplate template = templateRepository.findByIdAndCompanyIdAndDeletedAtIsNull(
                r.templateId(), r.companyId()
            ).orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada"));

            document.setTemplateId(r.templateId());

            String content = template.getContent();
            if (r.variables() != null && !r.variables().isEmpty()) {
                content = replaceVariables(content, r.variables());
            }
            document.setContent(content);
        }

        if (r.leadId() != null) {
            document.setLeadId(r.leadId());
        }

        if (r.propertyId() != null) {
            document.setPropertyId(r.propertyId());
        }

        if (r.metadata() != null) {
            document.setMetadata(r.metadata());
        }

        return repository.save(document);
    }

    private String replaceVariables(String content, Map<String, String> variables) {
        String result = content;
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String variable = matcher.group(1).trim();
            String value = variables.getOrDefault(variable, "");
            result = result.replace("{{" + matcher.group(1) + "}}", value);
        }

        return result;
    }

    public List<Document> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId);
    }

    public List<Document> listByLead(UUID leadId, UUID companyId) {
        return repository.findByLeadIdAndCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(leadId, companyId);
    }

    public List<Document> listByProperty(UUID propertyId, UUID companyId) {
        return repository.findByPropertyIdAndCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(propertyId, companyId);
    }

    public List<Document> listByStatus(UUID companyId, DocumentStatus status) {
        return repository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, status);
    }

    public Document get(UUID documentId, UUID companyId) {
        return repository.findByIdAndCompanyIdAndDeletedAtIsNull(documentId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
    }

    @Transactional
    public Document updateStatus(UUID documentId, UUID companyId, DocumentStatus status) {
        Document document = get(documentId, companyId);
        document.setStatus(status);
        return repository.save(document);
    }

    @Transactional
    public Document sendForSignature(UUID documentId, UUID companyId) {
        Document document = get(documentId, companyId);

        List<DocumentSignature> signatures = signatureRepository.findByDocumentIdOrderByCreatedAtAsc(documentId);
        if (signatures.isEmpty()) {
            throw new IllegalArgumentException("El documento no tiene firmantes asignados");
        }

        document.setStatus(DocumentStatus.PENDING_SIGNATURE);
        return repository.save(document);
    }

    @Transactional
    public void delete(UUID documentId, UUID companyId) {
        Document document = get(documentId, companyId);
        document.softDelete();
        repository.save(document);
    }

    @Transactional
    public void checkSignatureCompletion(UUID documentId) {
        Document document = repository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        List<DocumentSignature> allSignatures = signatureRepository.findByDocumentIdOrderByCreatedAtAsc(documentId);
        long signedCount = signatureRepository.countByDocumentIdAndStatus(documentId, SignatureStatus.SIGNED);

        if (signedCount == allSignatures.size() && allSignatures.size() > 0) {
            document.setStatus(DocumentStatus.SIGNED);
        } else if (signedCount > 0) {
            document.setStatus(DocumentStatus.PARTIALLY_SIGNED);
        }

        repository.save(document);
    }
}
