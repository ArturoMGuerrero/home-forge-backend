package com.casaflow.document.service;

import com.casaflow.document.domain.DocumentTemplate;
import com.casaflow.document.domain.DocumentType;
import com.casaflow.document.dto.CreateTemplateRequest;
import com.casaflow.document.repository.DocumentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentTemplateService {
    private final DocumentTemplateRepository repository;

    public DocumentTemplateService(DocumentTemplateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DocumentTemplate create(CreateTemplateRequest r) {
        DocumentTemplate template = new DocumentTemplate(
            r.companyId(),
            r.name(),
            r.description(),
            r.documentType(),
            r.content()
        );

        if (r.category() != null) {
            template.setCategory(r.category());
        }

        if (r.variables() != null) {
            template.setVariables(r.variables());
        }

        if (r.isDefault() != null && r.isDefault()) {
            // Remove default flag from other templates of same type
            repository.findByCompanyIdAndIsDefaultAndDocumentTypeAndDeletedAtIsNull(
                r.companyId(), true, r.documentType()
            ).ifPresent(existing -> {
                existing.setDefault(false);
                repository.save(existing);
            });

            template.setDefault(true);
        }

        return repository.save(template);
    }

    public List<DocumentTemplate> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(companyId);
    }

    public List<DocumentTemplate> listActive(UUID companyId) {
        return repository.findByCompanyIdAndActiveAndDeletedAtIsNullOrderByNameAsc(companyId, true);
    }

    public List<DocumentTemplate> listByType(UUID companyId, DocumentType documentType) {
        return repository.findByCompanyIdAndDocumentTypeAndDeletedAtIsNull(companyId, documentType);
    }

    public DocumentTemplate get(UUID templateId, UUID companyId) {
        return repository.findByIdAndCompanyIdAndDeletedAtIsNull(templateId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada"));
    }

    @Transactional
    public DocumentTemplate update(UUID templateId, UUID companyId, String content) {
        DocumentTemplate template = get(templateId, companyId);
        template.setContent(content);
        template.incrementVersion();
        return repository.save(template);
    }

    @Transactional
    public void delete(UUID templateId, UUID companyId) {
        DocumentTemplate template = get(templateId, companyId);
        template.softDelete();
        repository.save(template);
    }

    @Transactional
    public DocumentTemplate toggleActive(UUID templateId, UUID companyId) {
        DocumentTemplate template = get(templateId, companyId);
        template.setActive(!template.isActive());
        return repository.save(template);
    }
}
