package com.homeforge.document.controller;

import com.homeforge.document.domain.Document;
import com.homeforge.document.domain.DocumentStatus;
import com.homeforge.document.dto.CreateDocumentRequest;
import com.homeforge.document.service.DocumentGenerationService;
import com.homeforge.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Contratos y documentos generados a partir de plantillas. (Los archivos que se suben, como
 * identificaciones o escrituras, viven aparte en /api/documents.)
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final DocumentGenerationService service;

    public ContractController(DocumentGenerationService service) {
        this.service = service;
    }

    public record ChangeStatusRequest(@NotNull DocumentStatus status) {}

    @GetMapping
    public List<Document> list(@RequestParam UUID companyId, @RequestParam(required = false) DocumentStatus status) {
        return status == null ? service.listByCompany(companyId) : service.listByStatus(companyId, status);
    }

    @GetMapping("/lead/{leadId}")
    public List<Document> listByLead(@PathVariable UUID leadId, @RequestParam UUID companyId) {
        return service.listByLead(leadId, companyId);
    }

    @GetMapping("/property/{propertyId}")
    public List<Document> listByProperty(@PathVariable UUID propertyId, @RequestParam UUID companyId) {
        return service.listByProperty(propertyId, companyId);
    }

    @GetMapping("/{id}")
    public Document get(@PathVariable UUID id, @RequestParam UUID companyId) {
        return service.get(id, companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Document create(@Valid @RequestBody CreateDocumentRequest request) {
        // El autor es siempre el usuario de la sesión, no lo que diga el cliente.
        UUID author = CurrentUser.get().map(CurrentUser::userId)
                .orElseThrow(() -> new AccessDeniedException("Inicia sesión para continuar"));
        return service.create(new CreateDocumentRequest(
                request.companyId(), request.templateId(), request.name(), request.documentType(), author,
                request.leadId(), request.propertyId(), request.variables(), request.metadata()));
    }

    @PatchMapping("/{id}/status")
    public Document changeStatus(@PathVariable UUID id, @RequestParam UUID companyId,
                                 @Valid @RequestBody ChangeStatusRequest request) {
        return service.updateStatus(id, companyId, request.status());
    }

    @PostMapping("/{id}/send-for-signature")
    public Document sendForSignature(@PathVariable UUID id, @RequestParam UUID companyId) {
        return service.sendForSignature(id, companyId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @RequestParam UUID companyId) {
        service.delete(id, companyId);
    }
}
