package com.homeforge.subscription;

import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.subscription.exception.SubscriptionExpiredException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SubscriptionValidatorTest {

    @Test
    void allowsActiveSubscription() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("ACTIVE");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> validator.validateActiveSubscription(companyId, "crear prospectos"));
    }

    @Test
    void allowsTrialSubscription() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("TRIAL");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> validator.validateActiveSubscription(companyId, "crear prospectos"));
    }

    @Test
    void rejectsExpiredSubscription() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("EXPIRED");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        SubscriptionExpiredException exception = assertThrows(
            SubscriptionExpiredException.class,
            () -> validator.validateActiveSubscription(companyId, "crear nuevos prospectos")
        );

        assertTrue(exception.getMessage().contains("Tu suscripción ha expirado"));
        assertTrue(exception.getMessage().contains("crear nuevos prospectos"));
    }

    @Test
    void rejectsSuspendedSubscription() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("SUSPENDED");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(
            SubscriptionExpiredException.class,
            () -> validator.validateActiveSubscription(companyId, "crear propiedades")
        );
    }

    @Test
    void rejectsCancelledSubscription() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("CANCELLED");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(
            SubscriptionExpiredException.class,
            () -> validator.validateActiveSubscription(companyId, "editar configuración")
        );
    }

    @Test
    void hasActiveSubscriptionReturnsTrueForActive() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("ACTIVE");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertTrue(validator.hasActiveSubscription(companyId));
    }

    @Test
    void hasActiveSubscriptionReturnsTrueForTrial() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("TRIAL");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertTrue(validator.hasActiveSubscription(companyId));
    }

    @Test
    void hasActiveSubscriptionReturnsFalseForExpired() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        Company company = Mockito.mock(Company.class);
        when(company.getComputedSubscriptionStatus()).thenReturn("EXPIRED");
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertFalse(validator.hasActiveSubscription(companyId));
    }

    @Test
    void throwsWhenCompanyNotFound() {
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        SubscriptionValidator validator = new SubscriptionValidator(companyRepository);

        UUID companyId = UUID.randomUUID();
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validateActiveSubscription(companyId, "cualquier acción")
        );
    }
}
