package com.casaflow.subscription;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.subscription.exception.SubscriptionExpiredException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SubscriptionValidator {
    private final CompanyRepository companyRepository;

    public SubscriptionValidator(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void validateActiveSubscription(UUID companyId, String action) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        String status = company.getComputedSubscriptionStatus();

        // Permitir si está en TRIAL o ACTIVE
        if ("TRIAL".equals(status) || "ACTIVE".equals(status)) {
            return;
        }

        // Bloquear si está EXPIRED, SUSPENDED o CANCELLED
        throw new SubscriptionExpiredException(
            String.format("Tu suscripción ha expirado. No puedes %s. Por favor renueva tu plan para continuar.", action)
        );
    }

    public boolean hasActiveSubscription(UUID companyId) {
        return companyRepository.findById(companyId)
                .map(company -> {
                    String status = company.getComputedSubscriptionStatus();
                    return "TRIAL".equals(status) || "ACTIVE".equals(status);
                })
                .orElse(false);
    }
}
