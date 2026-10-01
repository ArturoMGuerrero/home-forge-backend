package com.homeforge.lead;
import com.homeforge.lead.dto.CreateLeadRequest;
import com.homeforge.lead.dto.CreateLeadActivityRequest;
import com.homeforge.lead.domain.Lead;
import com.homeforge.lead.domain.LeadActivityType;
import com.homeforge.lead.repository.LeadRepository;
import com.homeforge.lead.repository.LeadActivityRepository;
import com.homeforge.lead.service.LeadService;
import com.homeforge.lead.service.FollowUpAutomationService;
import com.homeforge.lead.service.LeadScoringService;
import com.homeforge.lead.service.LeadAssignmentService;
import com.homeforge.subscription.SubscriptionValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.UUID;
import java.util.Optional;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
class LeadServiceTest {
 @Test void createsLead() {
   LeadRepository repo = Mockito.mock(LeadRepository.class);
   LeadActivityRepository activityRepository = Mockito.mock(LeadActivityRepository.class);
   SubscriptionValidator subscriptionValidator = Mockito.mock(SubscriptionValidator.class);
   FollowUpAutomationService followUpAutomationService = Mockito.mock(FollowUpAutomationService.class);
   LeadScoringService scoringService = Mockito.mock(LeadScoringService.class);
   LeadAssignmentService assignmentService = Mockito.mock(LeadAssignmentService.class);
   LeadService service = new LeadService(repo, activityRepository, subscriptionValidator, followUpAutomationService, scoringService, assignmentService);
   service.create(new CreateLeadRequest(UUID.randomUUID(), "John", "Smith", "john@test.com", "+19155551234", "SALE", null, "USD", "El Paso"));
   Mockito.verify(repo).save(any());
 }

 @Test void registersActivityAndUpdatesNextFollowUpForTheSameCompany() {
   LeadRepository repo = Mockito.mock(LeadRepository.class);
   LeadActivityRepository activityRepository = Mockito.mock(LeadActivityRepository.class);
   Lead lead = Mockito.mock(Lead.class);
   UUID companyId = UUID.randomUUID();
   UUID leadId = UUID.randomUUID();
   Instant nextFollowUp = Instant.now().plusSeconds(86400);
   Mockito.when(repo.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)).thenReturn(Optional.of(lead));

   SubscriptionValidator validator = Mockito.mock(SubscriptionValidator.class);
   FollowUpAutomationService followUpAutomationService = Mockito.mock(FollowUpAutomationService.class);
   LeadScoringService scoringService = Mockito.mock(LeadScoringService.class);
   LeadAssignmentService assignmentService = Mockito.mock(LeadAssignmentService.class);
   new LeadService(repo, activityRepository, validator, followUpAutomationService, scoringService, assignmentService).addActivity(leadId, new CreateLeadActivityRequest(
     companyId, LeadActivityType.CALL, "Solicitó opciones de tres recámaras.", null, nextFollowUp, null, null, null, null, null, null
   ));

   Mockito.verify(lead).setNextFollowUpAt(nextFollowUp);
   Mockito.verify(activityRepository).save(any());
 }

 @Test void rejectsActivityWhenLeadDoesNotBelongToCompany() {
   LeadRepository repo = Mockito.mock(LeadRepository.class);
   LeadActivityRepository activityRepository = Mockito.mock(LeadActivityRepository.class);
   UUID companyId = UUID.randomUUID();
   UUID leadId = UUID.randomUUID();
   Mockito.when(repo.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)).thenReturn(Optional.empty());

   SubscriptionValidator subscriptionValidator = Mockito.mock(SubscriptionValidator.class);
   FollowUpAutomationService followUpAutomationService = Mockito.mock(FollowUpAutomationService.class);
   LeadScoringService scoringService = Mockito.mock(LeadScoringService.class);
   LeadAssignmentService assignmentService = Mockito.mock(LeadAssignmentService.class);
   LeadService service = new LeadService(repo, activityRepository, subscriptionValidator, followUpAutomationService, scoringService, assignmentService);

   assertThrows(IllegalArgumentException.class, () -> service.addActivity(leadId, new CreateLeadActivityRequest(
     companyId, LeadActivityType.NOTE, "Seguimiento", null, null, null, null, null, null, null, null
   )));
 }
}
