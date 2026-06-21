package com.casaflow.property;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.property.domain.Property;
import com.casaflow.property.repository.PropertyRepository;
import com.casaflow.property.service.PublicPropertyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicPropertyServiceTest {

    @Test
    void includesTheCompanyThatOwnsTheProperty() {
        PropertyRepository propertyRepository = Mockito.mock(PropertyRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        Property property = Mockito.mock(Property.class);
        Company company = Mockito.mock(Company.class);
        UUID companyId = UUID.randomUUID();

        Mockito.when(property.getCompanyId()).thenReturn(companyId);
        Mockito.when(propertyRepository.findByPublishedTrueAndDeletedAtIsNullOrderByCreatedAtDesc())
                .thenReturn(List.of(property));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(company.getId()).thenReturn(companyId);
        Mockito.when(company.getName()).thenReturn("Inmobiliaria Horizonte");
        Mockito.when(company.getPublicEmail()).thenReturn("ventas@horizonte.example");
        Mockito.when(company.getPublicPhoneE164()).thenReturn("+524421234567");

        var listings = new PublicPropertyService(propertyRepository, companyRepository).published();

        assertEquals(1, listings.size());
        assertEquals(property, listings.get(0).property());
        assertEquals(companyId, listings.get(0).seller().companyId());
        assertEquals("Inmobiliaria Horizonte", listings.get(0).seller().companyName());
        assertEquals("ventas@horizonte.example", listings.get(0).seller().email());
        assertEquals("+524421234567", listings.get(0).seller().phoneE164());
    }

    @Test
    void returnsOnlyAnExplicitlyPublishedPropertyById() {
        PropertyRepository propertyRepository = Mockito.mock(PropertyRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        Property property = Mockito.mock(Property.class);
        Company company = Mockito.mock(Company.class);
        UUID propertyId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Mockito.when(property.getCompanyId()).thenReturn(companyId);
        Mockito.when(propertyRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId))
                .thenReturn(Optional.of(property));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(company.getId()).thenReturn(companyId);

        var listing = new PublicPropertyService(propertyRepository, companyRepository).getPublished(propertyId);

        assertEquals(property, listing.property());
        Mockito.verify(propertyRepository).findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId);
    }
}
