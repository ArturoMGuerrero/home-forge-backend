package com.casaflow.document.controller;

import com.casaflow.document.domain.StoredDocument;
import com.casaflow.document.dto.DocumentResponse;
import com.casaflow.document.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service) { this.service = service; }

    @GetMapping
    public List<DocumentResponse> list(@RequestParam UUID companyId) { return service.list(companyId); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse create(@RequestParam UUID companyId, @RequestParam(required = false) UUID leadId,
                                   @RequestParam(required = false) UUID propertyId, @RequestParam String documentType,
                                   @RequestParam(defaultValue = "PENDING") String status,
                                   @RequestParam(required = false) String notes, @RequestPart("file") MultipartFile file) {
        return service.create(companyId, leadId, propertyId, documentType, status, notes, file);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id, @RequestParam UUID companyId) {
        StoredDocument document = service.get(id, companyId);
        return ResponseEntity.ok()
                .contentType(document.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName().replace("\"", "") + "\"")
                .body(service.resource(id, companyId));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> view(@PathVariable UUID id, @RequestParam UUID companyId) {
        StoredDocument document = service.get(id, companyId);
        return ResponseEntity.ok()
                .contentType(document.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getFileName().replace("\"", "") + "\"")
                .body(service.resource(id, companyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam UUID companyId) {
        service.delete(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
