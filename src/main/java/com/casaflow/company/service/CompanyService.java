package com.casaflow.company.service;

import com.casaflow.company.domain.Company;
import com.casaflow.company.dto.CompanyProfileRequest;
import com.casaflow.company.dto.CompanyProfileResponse;
import com.casaflow.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse get(UUID companyId) {
        return CompanyProfileResponse.from(find(companyId));
    }

    @Transactional
    public CompanyProfileResponse update(UUID companyId, CompanyProfileRequest request) {
        Company company = find(companyId);
        company.updateProfile(
                request.name().trim(),
                request.countryCode().trim().toUpperCase(),
                request.stateCode().trim(),
                clean(request.city()),
                clean(request.address()),
                clean(request.postalCode()),
                clean(request.publicEmail()),
                clean(request.publicPhoneE164()),
                clean(request.websiteUrl()),
                clean(request.publicDescription()),
                clean(request.mission()),
                clean(request.vision()),
                clean(request.professionalLicense()),
                request.yearsExperience()
        );
        return CompanyProfileResponse.from(repository.save(company));
    }

    private Company find(UUID companyId) {
        return repository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
