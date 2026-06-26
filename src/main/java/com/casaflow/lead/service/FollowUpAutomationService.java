package com.casaflow.lead.service;

import com.casaflow.lead.domain.*;
import com.casaflow.lead.dto.CreateFollowUpTaskRequest;
import com.casaflow.lead.repository.LeadRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class FollowUpAutomationService {
    private final LeadRepository leadRepository;
    private final FollowUpTaskService followUpTaskService;

    public FollowUpAutomationService(
        LeadRepository leadRepository,
        FollowUpTaskService followUpTaskService
    ) {
        this.leadRepository = leadRepository;
        this.followUpTaskService = followUpTaskService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void createAutomatedFollowUpTasks() {
        followUpTaskService.markOverdueTasks();
    }

    public void createTasksForStatusChange(UUID leadId, UUID companyId, LeadStatus oldStatus, LeadStatus newStatus) {
        Lead lead = leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)
            .orElse(null);

        if (lead == null) return;

        switch (newStatus) {
            case CONTACTED -> createFollowUpTask(
                lead,
                "Seguimiento después de contacto inicial",
                "Verificar interés y enviar propiedades relevantes",
                FollowUpTaskType.FOLLOW_UP,
                2,
                FollowUpTaskPriority.MEDIUM
            );
            case QUALIFIED -> createFollowUpTask(
                lead,
                "Agendar visita",
                "Contactar para agendar tour de propiedades",
                FollowUpTaskType.SCHEDULE_TOUR,
                1,
                FollowUpTaskPriority.HIGH
            );
            case TOUR_SCHEDULED -> createFollowUpTask(
                lead,
                "Recordatorio de visita",
                "Confirmar asistencia 24h antes",
                FollowUpTaskType.CALL,
                0,
                FollowUpTaskPriority.HIGH
            );
            case TOUR_COMPLETED -> createFollowUpTask(
                lead,
                "Seguimiento post-visita",
                "Obtener feedback y enviar siguiente paso",
                FollowUpTaskType.FOLLOW_UP,
                1,
                FollowUpTaskPriority.HIGH
            );
            case OFFER_MADE -> createFollowUpTask(
                lead,
                "Seguimiento de oferta",
                "Verificar decisión y resolver dudas",
                FollowUpTaskType.CALL,
                2,
                FollowUpTaskPriority.URGENT
            );
            case UNDER_CONTRACT -> createFollowUpTask(
                lead,
                "Enviar documentos de contrato",
                "Preparar y enviar documentación legal",
                FollowUpTaskType.SEND_CONTRACT,
                1,
                FollowUpTaskPriority.URGENT
            );
        }
    }

    private void createFollowUpTask(
        Lead lead,
        String title,
        String description,
        FollowUpTaskType taskType,
        int daysFromNow,
        FollowUpTaskPriority priority
    ) {
        Instant scheduledFor = Instant.now().plus(daysFromNow, ChronoUnit.DAYS);

        CreateFollowUpTaskRequest request = new CreateFollowUpTaskRequest(
            lead.getCompanyId(),
            lead.getId(),
            title,
            description,
            taskType,
            scheduledFor,
            null,
            priority
        );

        followUpTaskService.create(request);
    }
}
