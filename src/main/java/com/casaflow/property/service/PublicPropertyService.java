package com.casaflow.property.service;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.property.dto.PublicPropertyResponse;
import com.casaflow.property.dto.SellerContact;
import com.casaflow.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicPropertyService {

    private final PropertyRepository propertyRepository;
    private final CompanyRepository companyRepository;

    public PublicPropertyService(
            PropertyRepository propertyRepository,
            CompanyRepository companyRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<PublicPropertyResponse> published() {
        return propertyRepository.findByPublishedTrueAndDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicPropertyResponse getPublished(java.util.UUID propertyId) {
        return propertyRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId)
                .map(this::response)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad pública no encontrada."));
    }

    private PublicPropertyResponse response(com.casaflow.property.domain.Property property) {
        Company company = companyRepository.findById(property.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
        return new PublicPropertyResponse(
                property,
                new SellerContact(
                        company.getId(),
                        company.getName(),
                        company.getPublicEmail(),
                        company.getPublicPhoneE164()
                )
        );
    }
}
