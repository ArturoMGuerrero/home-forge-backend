package com.casaflow.company.controller;

import com.casaflow.company.dto.CompanyProfileRequest;
import com.casaflow.company.dto.CompanyProfileResponse;
import com.casaflow.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @GetMapping("/public/{companyId}")
    public CompanyProfileResponse publicProfile(@PathVariable UUID companyId) {
        return service.get(companyId);
    }
}
