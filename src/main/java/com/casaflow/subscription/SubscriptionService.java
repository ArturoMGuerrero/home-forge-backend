package com.casaflow.subscription;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.subscription.dto.SubscriptionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubscriptionService {
    private final CompanyRepository companyRepository;

    public SubscriptionService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse get(UUID companyId) {
        return SubscriptionResponse.from(findCompany(companyId));
    }

    @Transactional
    public SubscriptionResponse changePlan(UUID companyId, PlanCode planCode) {
        Company company = findCompany(companyId);
        company.changePlan(planCode);
        return SubscriptionResponse.from(companyRepository.save(company));
    }

    private Company findCompany(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }
}
