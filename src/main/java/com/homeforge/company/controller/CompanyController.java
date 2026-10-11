package com.homeforge.company.controller;

import com.homeforge.company.dto.BusinessHours;
import com.homeforge.company.dto.CompanyProfileRequest;
import com.homeforge.company.dto.CompanyProfileResponse;
import com.homeforge.company.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Empresa", description = "Perfil de la inmobiliaria, perfil público y horario de atención.")
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping("/{companyId}")
    public CompanyProfileResponse get(@PathVariable UUID companyId) {
        return service.get(companyId);
    }

    @PutMapping("/{companyId}")
    public CompanyProfileResponse update(
            @PathVariable UUID companyId,
            @Valid @RequestBody CompanyProfileRequest request
    ) {
        return service.update(companyId, request);
    }

    /** Horario de atención: lo usan los selectores de hora de las citas y el calendario. */
    @GetMapping("/{companyId}/business-hours")
    public BusinessHours businessHours(@PathVariable UUID companyId) {
        return service.businessHours(companyId);
    }

    @PutMapping("/{companyId}/business-hours")
    public BusinessHours updateBusinessHours(
            @PathVariable UUID companyId,
            @Valid @RequestBody BusinessHours request
    ) {
        return service.updateBusinessHours(companyId, request);
    }

    @GetMapping("/public/{companyId}")
    public CompanyProfileResponse publicProfile(@PathVariable UUID companyId) {
        return service.get(companyId);
    }
}
