package com.casaflow.match.service;

import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.match.domain.LeadPropertyMatch;
import com.casaflow.match.dto.MatchRequest;
import com.casaflow.match.dto.MatchResponse;
import com.casaflow.match.repository.LeadPropertyMatchRepository;
import com.casaflow.property.domain.Property;
import com.casaflow.property.repository.PropertyRepository;
import com.casaflow.subscription.SubscriptionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LeadPropertyMatchService {
    private final LeadPropertyMatchRepository repository;
    private final LeadRepository leadRepository;
    private final PropertyRepository propertyRepository;
    private final SubscriptionValidator subscriptionValidator;

    public LeadPropertyMatchService(LeadPropertyMatchRepository repository, LeadRepository leadRepository, PropertyRepository propertyRepository, SubscriptionValidator subscriptionValidator) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.propertyRepository = propertyRepository;
        this.subscriptionValidator = subscriptionValidator;
    }

    public List<MatchResponse> list(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId).stream()
                .map(match -> response(match, companyId))
                .toList();
    }

    public MatchResponse create(MatchRequest request) {
        subscriptionValidator.validateActiveSubscription(request.companyId(), "crear nuevas asignaciones de propiedades");
        if (repository.existsByCompanyIdAndLeadIdAndPropertyIdAndDeletedAtIsNull(request.companyId(), request.leadId(), request.propertyId())) {
            throw new IllegalArgumentException("Esta propiedad ya está asignada al prospecto.");
        }
        Lead lead = findLead(request.leadId(), request.companyId());
        Property property = findProperty(request.propertyId(), request.companyId());
        LeadPropertyMatch match = repository.save(new LeadPropertyMatch(request.companyId(), request.leadId(),
                request.propertyId(), request.status() == null ? "SUGGESTED" : request.status(), clean(request.notes())));
        return MatchResponse.of(match, lead.getFirstName() + " " + lead.getLastName(), property.getTitle(), property.getCode());
    }

    @Transactional
    public MatchResponse update(UUID id, MatchRequest request) {
        LeadPropertyMatch match = repository.findByIdAndCompanyIdAndDeletedAtIsNull(id, request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada."));
        match.update(request.status() == null ? match.getStatus() : request.status(), clean(request.notes()));
        return response(repository.save(match), request.companyId());
    }

    @Transactional
    public void delete(UUID id, UUID companyId) {
        LeadPropertyMatch match = repository.findByIdAndCompanyIdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada."));
        match.softDelete();
        repository.save(match);
    }

    private MatchResponse response(LeadPropertyMatch match, UUID companyId) {
        Lead lead = findLead(match.getLeadId(), companyId);
        Property property = findProperty(match.getPropertyId(), companyId);
        return MatchResponse.of(match, lead.getFirstName() + " " + lead.getLastName(), property.getTitle(), property.getCode());
    }

    private Lead findLead(UUID id, UUID companyId) {
        return leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Prospecto no encontrado."));
    }

    private Property findProperty(UUID id, UUID companyId) {
        return propertyRepository.findByIdAndCompanyIdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada."));
    }

    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
