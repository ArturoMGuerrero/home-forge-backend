package com.casaflow.company.dto;

import com.casaflow.company.domain.Company;

import java.util.UUID;

public record CompanyProfileResponse(
        UUID id,
        String name,
        String countryCode,
        String stateCode,
        String city,
        String address,
        String postalCode,
        String publicEmail,
        String publicPhoneE164,
        String websiteUrl,
        String publicDescription,
        String mission,
        String vision,
        String professionalLicense,
        Integer yearsExperience
) {
    public static CompanyProfileResponse from(Company company) {
        return new CompanyProfileResponse(
                company.getId(),
                company.getName(),
                company.getCountryCode(),
                company.getStateCode(),
                company.getCity(),
                company.getAddress(),
                company.getPostalCode(),
                company.getPublicEmail(),
                company.getPublicPhoneE164(),
                company.getWebsiteUrl(),
                company.getPublicDescription(),
                company.getMission(),
                company.getVision(),
                company.getProfessionalLicense(),
                company.getYearsExperience()
        );
    }
}
