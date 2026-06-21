package com.casaflow.document.service;

import com.casaflow.document.domain.StoredDocument;
import com.casaflow.document.dto.DocumentResponse;
import com.casaflow.document.repository.StoredDocumentRepository;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.property.domain.Property;
import com.casaflow.property.repository.PropertyRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private final StoredDocumentRepository repository;
    private final LeadRepository leadRepository;
    private final PropertyRepository propertyRepository;
    private final DocumentStorage storage;

    public DocumentService(StoredDocumentRepository repository, LeadRepository leadRepository,
                           PropertyRepository propertyRepository, DocumentStorage storage) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.propertyRepository = propertyRepository;
        this.storage = storage;
    }

    public List<DocumentResponse> list(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId).stream()
                .map(document -> response(document, companyId))
                .toList();
    }

    public DocumentResponse create(UUID companyId, UUID leadId, UUID propertyId, String documentType,
                                   String status, String notes, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Selecciona un archivo.");
        if (file.getSize() > 8L * 1024 * 1024) throw new IllegalArgumentException("El archivo no puede superar 8 MB.");
        validateRelations(companyId, leadId, propertyId);
        String path = storage.store(companyId, file);
        StoredDocument document = repository.save(new StoredDocument(companyId, leadId, propertyId,
                documentType.trim(), file.getOriginalFilename() == null ? "documento" : file.getOriginalFilename(),
                status == null || status.isBlank() ? "PENDING" : status, path, file.getContentType(),
                file.getSize(), clean(notes)));
        return response(document, companyId);
    }

    public StoredDocument get(UUID id, UUID companyId) {
        return repository.findByIdAndCompanyIdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado."));
    }

    public Resource resource(UUID id, UUID companyId) {
        StoredDocument document = get(id, companyId);
        Resource resource = new FileSystemResource(storage.resolve(document.getFilePath()));
        if (!resource.exists()) throw new IllegalArgumentException("El archivo ya no está disponible.");
        return resource;
    }

    @Transactional
    public void delete(UUID id, UUID companyId) {
        StoredDocument document = get(id, companyId);
        document.softDelete();
        repository.save(document);
        if (document.getFilePath() != null) storage.delete(document.getFilePath());
    }

    private DocumentResponse response(StoredDocument document, UUID companyId) {
        String leadName = null;
        String propertyTitle = null;
        if (document.getLeadId() != null) {
            Lead lead = leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(document.getLeadId(), companyId).orElse(null);
            if (lead != null) leadName = lead.getFirstName() + " " + lead.getLastName();
        }
        if (document.getPropertyId() != null) {
            Property property = propertyRepository.findByIdAndCompanyIdAndDeletedAtIsNull(document.getPropertyId(), companyId).orElse(null);
            if (property != null) propertyTitle = property.getTitle();
        }
        return DocumentResponse.of(document, leadName, propertyTitle);
    }

    private void validateRelations(UUID companyId, UUID leadId, UUID propertyId) {
        if (leadId != null) leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Prospecto no encontrado."));
        if (propertyId != null) propertyRepository.findByIdAndCompanyIdAndDeletedAtIsNull(propertyId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada."));
    }

    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
