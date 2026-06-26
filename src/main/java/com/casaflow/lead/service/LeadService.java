package com.casaflow.lead.service;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.domain.LeadStatus;
import com.casaflow.lead.dto.ChangeLeadStatusRequest;
import com.casaflow.lead.dto.CreateLeadRequest;
import com.casaflow.lead.dto.CreateLeadActivityRequest;
import com.casaflow.lead.dto.UpdateLeadRequest;
import com.casaflow.lead.domain.LeadActivity;
import com.casaflow.lead.repository.LeadActivityRepository;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.subscription.SubscriptionValidator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
@Service
public class LeadService {
  private final LeadRepository repo;
  private final LeadActivityRepository activityRepository;
  private final SubscriptionValidator subscriptionValidator;
  private final FollowUpAutomationService followUpAutomationService;
  private final LeadScoringService scoringService;
  private final LeadAssignmentService assignmentService;

  public LeadService(
      LeadRepository repo,
      LeadActivityRepository activityRepository,
      SubscriptionValidator subscriptionValidator,
      @Lazy FollowUpAutomationService followUpAutomationService,
      @Lazy LeadScoringService scoringService,
      @Lazy LeadAssignmentService assignmentService
  ){
    this.repo=repo;
    this.activityRepository=activityRepository;
    this.subscriptionValidator=subscriptionValidator;
    this.followUpAutomationService=followUpAutomationService;
    this.scoringService=scoringService;
    this.assignmentService=assignmentService;
  }
  public Lead create(CreateLeadRequest r){
    subscriptionValidator.validateActiveSubscription(r.companyId(), "crear nuevos prospectos");
    Lead lead = new Lead(
      r.companyId(), clean(r.firstName()), clean(r.lastName()), clean(r.email()), clean(r.phoneE164()),
      normalizeListingType(r.listingType()), r.budgetMax(), clean(r.currencyCode()), clean(r.city())
    );

    UUID assignedUserId = assignmentService.assignLead(lead);
    if (assignedUserId != null) {
      lead.setAssignedTo(assignedUserId.toString());
    }

    Lead saved = repo.save(lead);
    scoringService.calculateScore(saved.getId(), saved.getCompanyId());
    return saved;
  }
  public List<Lead> byCompany(UUID companyId){ return repo.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId); }
  public Lead get(UUID leadId, UUID companyId){ return find(leadId, companyId); }
  @Transactional
  public Lead update(UUID leadId, UpdateLeadRequest r){
    if (r.budgetMin() != null && r.budgetMax() != null && r.budgetMin().compareTo(r.budgetMax()) > 0) {
      throw new IllegalArgumentException("El presupuesto mínimo no puede superar al máximo.");
    }
    Lead lead = find(leadId, r.companyId());
    LeadStatus oldStatus = lead.getStatus();
    lead.update(
      clean(r.firstName()), clean(r.lastName()), clean(r.email()), clean(r.phoneE164()), clean(r.source()),
      r.status(), normalizeListingType(r.listingType()), r.budgetMin(), r.budgetMax(), clean(r.currencyCode()),
      clean(r.countryCode()), clean(r.stateCode()), clean(r.city()), clean(r.propertyType()),
      r.bedroomsMin(), r.bathroomsMin(), clean(r.financingType()), r.priority(),
      clean(r.assignedTo()), r.nextFollowUpAt(), clean(r.notes())
    );
    Lead saved = repo.save(lead);

    scoringService.calculateScore(leadId, r.companyId());

    if (oldStatus != r.status()) {
      followUpAutomationService.createTasksForStatusChange(leadId, r.companyId(), oldStatus, r.status());
    }

    return saved;
  }

  @Transactional
  public Lead changeStatus(UUID leadId, ChangeLeadStatusRequest r){
    Lead lead = find(leadId, r.companyId());
    LeadStatus oldStatus = lead.getStatus();
    lead.setStatus(r.status());
    Lead saved = repo.save(lead);

    scoringService.calculateScore(leadId, r.companyId());

    if (oldStatus != r.status()) {
      followUpAutomationService.createTasksForStatusChange(leadId, r.companyId(), oldStatus, r.status());
    }

    return saved;
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
    LeadActivity activity = new LeadActivity(
      r.companyId(), leadId, r.activityType(), r.notes().trim(),
      r.occurredAt() == null ? Instant.now() : r.occurredAt(), r.nextFollowUpAt()
    );

    if (r.userId() != null) activity.setUserId(r.userId());
    if (r.durationMinutes() != null) activity.setDurationMinutes(r.durationMinutes());
    if (r.outcome() != null) activity.setOutcome(r.outcome());
    if (r.propertyId() != null) activity.setPropertyId(r.propertyId());
    if (r.attachments() != null) activity.setAttachments(r.attachments());
    if (r.metadata() != null) activity.setMetadata(r.metadata());

    LeadActivity saved = activityRepository.save(activity);

    scoringService.calculateScore(leadId, r.companyId());

    return saved;
  }
  @Transactional
  public void deleteActivity(UUID leadId, UUID activityId, UUID companyId){
    find(leadId, companyId);
    LeadActivity activity = activityRepository.findByIdAndLeadIdAndCompanyId(activityId, leadId, companyId)
      .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada."));
    activityRepository.delete(activity);
  }

  public List<LeadActivity> activitiesByUser(UUID userId, UUID companyId){
    return activityRepository.findByUserIdAndCompanyIdOrderByOccurredAtDesc(userId, companyId);
  }

  public List<LeadActivity> activitiesByProperty(UUID propertyId, UUID companyId){
    return activityRepository.findByPropertyIdAndCompanyIdOrderByOccurredAtDesc(propertyId, companyId);
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
