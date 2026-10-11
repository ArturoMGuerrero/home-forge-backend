package com.homeforge.lead.controller;

import com.homeforge.auth.service.LoginAttemptService;
import com.homeforge.auth.service.LoginAttemptService.Action;
import com.homeforge.lead.dto.PublicInquiryRequest;
import com.homeforge.lead.service.PublicInquiryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Formulario "Me interesa" del sitio público: no requiere sesión y tiene límite de envíos por IP. */
@Tag(name = "Sitio público", description = "Vista previa (Open Graph) para WhatsApp y redes al compartir una propiedad, y formulario \"Me interesa\".")
@RestController
public class PublicInquiryController {

    private final PublicInquiryService service;
    private final LoginAttemptService attempts;

    public PublicInquiryController(PublicInquiryService service, LoginAttemptService attempts) {
        this.service = service;
        this.attempts = attempts;
    }

    @PostMapping("/api/properties/public/{propertyId}/inquiries")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submit(
            @PathVariable UUID propertyId,
            @Valid @RequestBody PublicInquiryRequest request,
            HttpServletRequest http
    ) {
        attempts.checkRate(Action.PUBLIC_INQUIRY, http.getRemoteAddr());
        service.submit(propertyId, request);
    }
}
