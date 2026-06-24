package com.casaflow.subscription;

import com.casaflow.lead.dto.CreateLeadRequest;
import com.casaflow.lead.service.LeadService;
import com.casaflow.property.dto.CreatePropertyRequest;
import com.casaflow.property.domain.ListingType;
import com.casaflow.property.domain.PropertyStatus;
import com.casaflow.property.service.PropertyService;
import com.casaflow.subscription.exception.SubscriptionExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class SubscriptionValidationIntegrationTest {

    @Autowired
    private LeadService leadService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID activeCompanyId;
    private UUID trialCompanyId;
    private UUID expiredCompanyId;

    @BeforeEach
    void setUp() {
        activeCompanyId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO companies (id, name, country_code, state_code, default_currency, timezone, plan_code, subscription_status, created_at) " +
            "VALUES (?, 'Empresa Activa', 'MX', 'QRO', 'MXN', 'America/Mexico_City', 'PROFESSIONAL', 'ACTIVE', CURRENT_TIMESTAMP)",
            activeCompanyId
        );

        trialCompanyId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO companies (id, name, country_code, state_code, default_currency, timezone, plan_code, subscription_status, created_at) " +
            "VALUES (?, 'Empresa en Trial', 'MX', 'QRO', 'MXN', 'America/Mexico_City', 'STARTER', 'TRIAL', CURRENT_TIMESTAMP)",
            trialCompanyId
        );

        expiredCompanyId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO companies (id, name, country_code, state_code, default_currency, timezone, plan_code, subscription_status, created_at) " +
            "VALUES (?, 'Empresa Expirada', 'MX', 'QRO', 'MXN', 'America/Mexico_City', 'PROFESSIONAL', 'EXPIRED', CURRENT_TIMESTAMP)",
            expiredCompanyId
        );
    }

    @Test
    void activeCompanyCanCreateLead() {
        CreateLeadRequest request = new CreateLeadRequest(
            activeCompanyId,
            "Juan",
            "Pérez",
            "juan@test.com",
            "+525555555555",
            "SALE",
            null,
            "MXN",
            "Querétaro"
        );

        assertDoesNotThrow(() -> leadService.create(request));
    }

    @Test
    void trialCompanyCanCreateLead() {
        CreateLeadRequest request = new CreateLeadRequest(
            trialCompanyId,
            "María",
            "González",
            "maria@test.com",
            "+525555555556",
            "SALE",
            null,
            "MXN",
            "Querétaro"
        );

        assertDoesNotThrow(() -> leadService.create(request));
    }

    @Test
    void expiredCompanyCannotCreateLead() {
        CreateLeadRequest request = new CreateLeadRequest(
            expiredCompanyId,
            "Pedro",
            "López",
            "pedro@test.com",
            "+525555555557",
            "SALE",
            null,
            "MXN",
            "Querétaro"
        );

        SubscriptionExpiredException exception = assertThrows(
            SubscriptionExpiredException.class,
            () -> leadService.create(request)
        );

        assertTrue(exception.getMessage().contains("Tu suscripción ha expirado"));
        assertTrue(exception.getMessage().contains("crear nuevos prospectos"));
    }

    @Test
    void activeCompanyCanCreateProperty() {
        CreatePropertyRequest request = new CreatePropertyRequest(
            activeCompanyId,
            "CF-100",
            "Casa de prueba",
            "HOUSE",
            ListingType.SALE,
            PropertyStatus.AVAILABLE,
            new BigDecimal("2000000"),
            "MXN",
            "MX",
            "Querétaro",
            "Querétaro",
            "Calle Principal 123",
            new BigDecimal("20.5888"),
            new BigDecimal("-100.3899"),
            3,
            new BigDecimal("2.5"),
            new BigDecimal("200"),
            new BigDecimal("150"),
            2,
            "Hermosa casa",
            null,
            true
        );

        assertDoesNotThrow(() -> propertyService.create(request));
    }

    @Test
    void trialCompanyCanCreateProperty() {
        CreatePropertyRequest request = new CreatePropertyRequest(
            trialCompanyId,
            "CF-101",
            "Departamento de prueba",
            "APARTMENT",
            ListingType.RENT,
            PropertyStatus.AVAILABLE,
            new BigDecimal("15000"),
            "MXN",
            "MX",
            "Querétaro",
            "Querétaro",
            "Av. Secundaria 456",
            new BigDecimal("20.5920"),
            new BigDecimal("-100.3950"),
            2,
            new BigDecimal("1.5"),
            new BigDecimal("100"),
            new BigDecimal("80"),
            1,
            "Departamento moderno",
            null,
            true
        );

        assertDoesNotThrow(() -> propertyService.create(request));
    }

    @Test
    void expiredCompanyCannotCreateProperty() {
        CreatePropertyRequest request = new CreatePropertyRequest(
            expiredCompanyId,
            "CF-102",
            "Local de prueba",
            "COMMERCIAL",
            ListingType.RENT,
            PropertyStatus.AVAILABLE,
            new BigDecimal("50000"),
            "MXN",
            "MX",
            "Querétaro",
            "Querétaro",
            "Blvd. Central 789",
            new BigDecimal("20.5800"),
            new BigDecimal("-100.3800"),
            0,
            new BigDecimal("1"),
            new BigDecimal("150"),
            new BigDecimal("150"),
            0,
            "Local comercial",
            null,
            false
        );

        SubscriptionExpiredException exception = assertThrows(
            SubscriptionExpiredException.class,
            () -> propertyService.create(request)
        );

        assertTrue(exception.getMessage().contains("Tu suscripción ha expirado"));
        assertTrue(exception.getMessage().contains("crear nuevas propiedades"));
    }

    @Test
    void suspendedCompanyCannotCreateLead() {
        UUID suspendedCompanyId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO companies (id, name, country_code, state_code, default_currency, timezone, plan_code, subscription_status, created_at) " +
            "VALUES (?, 'Empresa Suspendida', 'MX', 'QRO', 'MXN', 'America/Mexico_City', 'PROFESSIONAL', 'SUSPENDED', CURRENT_TIMESTAMP)",
            suspendedCompanyId
        );

        CreateLeadRequest request = new CreateLeadRequest(
            suspendedCompanyId,
            "Ana",
            "Martínez",
            "ana@test.com",
            "+525555555558",
            "SALE",
            null,
            "MXN",
            "Querétaro"
        );

        assertThrows(
            SubscriptionExpiredException.class,
            () -> leadService.create(request)
        );
    }

    @Test
    void cancelledCompanyCannotCreateProperty() {
        UUID cancelledCompanyId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO companies (id, name, country_code, state_code, default_currency, timezone, plan_code, subscription_status, created_at) " +
            "VALUES (?, 'Empresa Cancelada', 'MX', 'QRO', 'MXN', 'America/Mexico_City', 'PROFESSIONAL', 'CANCELLED', CURRENT_TIMESTAMP)",
            cancelledCompanyId
        );

        CreatePropertyRequest request = new CreatePropertyRequest(
            cancelledCompanyId,
            "CF-103",
            "Terreno de prueba",
            "LAND",
            ListingType.SALE,
            PropertyStatus.AVAILABLE,
            new BigDecimal("500000"),
            "MXN",
            "MX",
            "Querétaro",
            "Querétaro",
            "Carretera Norte Km 10",
            new BigDecimal("20.6000"),
            new BigDecimal("-100.4000"),
            0,
            BigDecimal.ZERO,
            new BigDecimal("1000"),
            new BigDecimal("1000"),
            0,
            "Terreno plano",
            null,
            false
        );

        assertThrows(
            SubscriptionExpiredException.class,
            () -> propertyService.create(request)
        );
    }
}
