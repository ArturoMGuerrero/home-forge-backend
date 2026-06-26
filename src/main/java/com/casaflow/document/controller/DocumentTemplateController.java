package com.casaflow.document.controller;

import com.casaflow.document.domain.DocumentTemplate;
import com.casaflow.document.domain.DocumentType;
import com.casaflow.document.dto.CreateTemplateRequest;
import com.casaflow.document.service.DocumentTemplateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/document-templates")
public class DocumentTemplateController {
    private final DocumentTemplateService service;

    public DocumentTemplateController(DocumentTemplateService service) {
        this.service = service;
    }

    @PostMapping
    public DocumentTemplate create(@Valid @RequestBody CreateTemplateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DocumentTemplate> list(@RequestParam UUID companyId, @RequestParam(required = false) Boolean active) {
        if (active != null && active) {
            return service.listActive(companyId);
        }
        return service.listByCompany(companyId);
    }

    @GetMapping("/by-type")
    public List<DocumentTemplate> listByType(
        @RequestParam UUID companyId,
        @RequestParam DocumentType documentType
    ) {
        return service.listByType(companyId, documentType);
    }

    @GetMapping("/{templateId}")
    public DocumentTemplate get(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        return service.get(templateId, companyId);
    }

    @PatchMapping("/{templateId}")
    public DocumentTemplate update(
        @PathVariable UUID templateId,
        @RequestParam UUID companyId,
        @RequestBody Map<String, String> body
    ) {
        String content = body.get("content");
        if (content == null) {
            throw new IllegalArgumentException("El contenido es requerido");
        }
        return service.update(templateId, companyId, content);
    }

    @PatchMapping("/{templateId}/toggle-active")
    public DocumentTemplate toggleActive(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        return service.toggleActive(templateId, companyId);
    }

    @DeleteMapping("/{templateId}")
    public void delete(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        service.delete(templateId, companyId);
    }
}
