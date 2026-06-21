package com.casaflow.company;

import com.casaflow.company.domain.Company;
import com.casaflow.company.dto.CompanyProfileRequest;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.company.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyServiceTest {

    @Test
    void updatesTheRequestedCompanyProfile() {
        CompanyRepository repository = Mockito.mock(CompanyRepository.class);
        Company company = new Company("Nombre anterior", "MX", "QRO", "MXN", "America/Mexico_City");
        UUID companyId = UUID.randomUUID();

        Mockito.when(repository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(repository.save(company)).thenReturn(company);

        var response = new CompanyService(repository).update(companyId, new CompanyProfileRequest(
                "Inmobiliaria Horizonte",
                "MX",
                "Querétaro",
                "Querétaro",
                "Av. Principal 100",
                "76000",
                "ventas@horizonte.example",
                "+524421234567",
                "https://horizonte.example",
                "Especialistas en vivienda residencial.",
                "Acompañar decisiones patrimoniales seguras.",
                "Ser la inmobiliaria de mayor confianza.",
                "LIC-QRO-123",
                12
        ));

        assertEquals("Inmobiliaria Horizonte", response.name());
        assertEquals("Querétaro", response.city());
        assertEquals("LIC-QRO-123", response.professionalLicense());
        assertEquals(12, response.yearsExperience());
        Mockito.verify(repository).save(company);
    }
}
