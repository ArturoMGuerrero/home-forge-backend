package com.casaflow.lead.service;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.dto.CreateLeadRequest;
import com.casaflow.lead.dto.CreateLeadActivityRequest;
import com.casaflow.lead.dto.UpdateLeadRequest;
import com.casaflow.lead.domain.LeadActivity;
import com.casaflow.lead.repository.LeadActivityRepository;
import com.casaflow.lead.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
@Service
public class LeadService {
  private final LeadRepository repo;
  private final LeadActivityRepository activityRepository;
  public LeadService(LeadRepository repo, LeadActivityRepository activityRepository){this.repo=repo; this.activityRepository=activityRepository;}
  public Lead create(CreateLeadRequest r){
    return repo.save(new Lead(
      r.companyId(), clean(r.firstName()), clean(r.lastName()), clean(r.email()), clean(r.phoneE164()),
      normalizeListingType(r.listingType()), r.budgetMax(), clean(r.currencyCode()), clean(r.city())
    ));
  }
  public List<Lead> byCompany(UUID companyId){ return repo.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId); }
  public Lead get(UUID leadId, UUID companyId){ return find(leadId, companyId); }
  @Transactional
  public Lead update(UUID leadId, UpdateLeadRequest r){
    if (r.budgetMin() != null && r.budgetMax() != null && r.budgetMin().compareTo(r.budgetMax()) > 0) {
      throw new IllegalArgumentException("El presupuesto mínimo no puede superar al máximo.");
    }
    Lead lead = find(leadId, r.companyId());
    lead.update(
      clean(r.firstName()), clean(r.lastName()), clean(r.email()), clean(r.phoneE164()), clean(r.source()),
      r.status(), normalizeListingType(r.listingType()), r.budgetMin(), r.budgetMax(), clean(r.currencyCode()),
      clean(r.countryCode()), clean(r.stateCode()), clean(r.city()), clean(r.propertyType()),
      r.bedroomsMin(), r.bathroomsMin(), clean(r.financingType()), r.priority(),
      clean(r.assignedTo()), r.nextFollowUpAt(), clean(r.notes())
    );
    return repo.save(lead);
  }
  public List<LeadActivity> activities(UUID leadId, UUID companyId){
    find(leadId, companyId);
    return activityRepository.findByLeadIdAndCompanyIdOrderByOccurredAtDesc(leadId, companyId);
  }
  @Transactional
  public LeadActivity addActivity(UUID leadId, CreateLeadActivityRequest r){
    Lead lead = find(leadId, r.companyId());
    if (r.nextFollowUpAt() != null) {
      lead.setNextFollowUpAt(r.nextFollowUpAt());
      repo.save(lead);
    }
    return activityRepository.save(new LeadActivity(
      r.companyId(), leadId, r.activityType(), r.notes().trim(),
      r.occurredAt() == null ? Instant.now() : r.occurredAt(), r.nextFollowUpAt()
    ));
  }
  private Lead find(UUID leadId, UUID companyId){
    return repo.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)
      .orElseThrow(() -> new IllegalArgumentException("Prospecto no encontrado."));
  }
  private static String clean(String value){ return value == null || value.isBlank() ? null : value.trim(); }

  private static String normalizeListingType(String listingType) {
    if (listingType == null || listingType.isBlank()) return null;
    String cleaned = listingType.trim();
    if ("Venta".equals(cleaned)) return "SALE";
    if ("Renta".equals(cleaned)) return "RENT";
    return cleaned; // Ya es SALE o RENT
  }
}
