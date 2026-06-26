package com.casaflow.document.controller;

import com.casaflow.document.domain.DocumentSignature;
import com.casaflow.document.dto.CreateSignatureRequest;
import com.casaflow.document.service.DocumentSignatureService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/document-signatures")
public class DocumentSignatureController {
    private final DocumentSignatureService service;

    public DocumentSignatureController(DocumentSignatureService service) {
        this.service = service;
    }

    @PostMapping
    public DocumentSignature create(@Valid @RequestBody CreateSignatureRequest request) {
        return service.create(request);
    }

    @GetMapping("/document/{documentId}")
    public List<DocumentSignature> listByDocument(@PathVariable UUID documentId) {
        return service.listByDocument(documentId);
    }

    @GetMapping("/document/{documentId}/pending")
    public List<DocumentSignature> listPending(@PathVariable UUID documentId) {
        return service.listPending(documentId);
    }

    @PostMapping("/{signatureId}/sign")
    public DocumentSignature sign(
        @PathVariable UUID signatureId,
        @RequestParam UUID companyId,
        @RequestBody Map<String, String> body,
        HttpServletRequest request
    ) {
        String signatureData = body.get("signatureData");
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        return service.sign(signatureId, companyId, signatureData, ipAddress, userAgent);
    }

    @PatchMapping("/{signatureId}/mark-sent")
    public DocumentSignature markAsSent(@PathVariable UUID signatureId, @RequestParam UUID companyId) {
        return service.markAsSent(signatureId, companyId);
    }

    @PatchMapping("/{signatureId}/mark-viewed")
    public DocumentSignature markAsViewed(@PathVariable UUID signatureId, @RequestParam UUID companyId) {
        return service.markAsViewed(signatureId, companyId);
    }

    @DeleteMapping("/{signatureId}")
    public void delete(@PathVariable UUID signatureId, @RequestParam UUID companyId) {
        service.delete(signatureId, companyId);
    }
}
