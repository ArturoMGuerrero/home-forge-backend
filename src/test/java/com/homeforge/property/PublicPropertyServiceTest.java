package com.homeforge.property;

import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.property.domain.Property;
import com.homeforge.property.dto.PublicProperty;
import com.homeforge.property.repository.PropertyRepository;
import com.homeforge.property.service.PublicPropertyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PublicPropertyServiceTest {

    @Test
    void includesTheCompanyThatOwnsTheProperty() {
        PropertyRepository propertyRepository = Mockito.mock(PropertyRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        Property property = Mockito.mock(Property.class);
        Company company = Mockito.mock(Company.class);
        UUID companyId = UUID.randomUUID();

        UUID propertyId = UUID.randomUUID();
        Mockito.when(property.getId()).thenReturn(propertyId);
        Mockito.when(property.getCompanyId()).thenReturn(companyId);
        Mockito.when(property.getOwnerName()).thenReturn("Dueño Privado");
        Mockito.when(propertyRepository.findByPublishedTrueAndDeletedAtIsNullOrderByCreatedAtDesc())
                .thenReturn(List.of(property));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(company.getId()).thenReturn(companyId);
        Mockito.when(company.getName()).thenReturn("Inmobiliaria Horizonte");
        Mockito.when(company.getPublicEmail()).thenReturn("ventas@horizonte.example");
        Mockito.when(company.getPublicPhoneE164()).thenReturn("+524421234567");

        var listings = new PublicPropertyService(propertyRepository, companyRepository).published();

        assertEquals(1, listings.size());
        assertEquals(propertyId, listings.get(0).property().id());
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

        Mockito.when(property.getId()).thenReturn(propertyId);
        Mockito.when(property.getCompanyId()).thenReturn(companyId);
        Mockito.when(propertyRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId))
                .thenReturn(Optional.of(property));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(company.getId()).thenReturn(companyId);

        var listing = new PublicPropertyService(propertyRepository, companyRepository).getPublished(propertyId);

        assertEquals(propertyId, listing.property().id());
        Mockito.verify(propertyRepository).findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId);
    }

    @Test
    void neverExposesOwnerContactData() {
        // El sitio público es anónimo: los datos del propietario son internos de la inmobiliaria.
        for (var component : PublicProperty.class.getRecordComponents()) {
            assertFalse(component.getName().toLowerCase().contains("owner"), "campo expuesto: " + component.getName());
        }
    }
}
