package com.casaflow.notification.service;

import com.casaflow.notification.domain.*;
import com.casaflow.notification.dto.CreateNotificationRequest;
import com.casaflow.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository repository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    private final PushNotificationService pushService;

    public NotificationService(
            NotificationRepository repository,
            EmailService emailService,
            WhatsAppService whatsAppService,
            PushNotificationService pushService
    ) {
        this.repository = repository;
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
        this.pushService = pushService;
    }

    public Notification create(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setCompanyId(request.getCompanyId());
        notification.setTemplateId(request.getTemplateId());
        notification.setNotificationType(request.getNotificationType());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : NotificationPriority.MEDIUM);
        notification.setRecipientType(request.getRecipientType());
        notification.setRecipientId(request.getRecipientId());
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientPhone(request.getRecipientPhone());
        notification.setRecipientName(request.getRecipientName());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setHtmlContent(request.getHtmlContent());
        notification.setLeadId(request.getLeadId());
        notification.setPropertyId(request.getPropertyId());
        notification.setTaskId(request.getTaskId());
        notification.setScheduledFor(request.getScheduledFor());
        notification.setMetadata(request.getMetadata());

        Notification saved = repository.save(notification);

        // Enviar inmediatamente si no está programado
        if (request.getScheduledFor() == null || request.getScheduledFor().isBefore(Instant.now())) {
            send(saved.getId(), saved.getCompanyId());
        }

        return saved;
    }

    public Notification send(UUID notificationId, UUID companyId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new RuntimeException("Notification already sent");
        }

        try {
            switch (notification.getNotificationType()) {
                case EMAIL:
                    emailService.send(notification);
                    break;
                case WHATSAPP:
                    whatsAppService.send(notification);
                    break;
                case PUSH:
                    pushService.send(notification);
                    break;
                case SMS:
                    // TODO: Implementar SMS
                    break;
            }

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailedAt(Instant.now());
            notification.setErrorMessage(e.getMessage());
        }

        notification.setUpdatedAt(Instant.now());
        return repository.save(notification);
    }

    public List<Notification> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId);
    }

    public List<Notification> listByStatus(UUID companyId, NotificationStatus status) {
        return repository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, status);
    }

    public List<Notification> listByLead(UUID leadId) {
        return repository.findByLeadIdAndDeletedAtIsNullOrderByCreatedAtDesc(leadId);
    }

    public List<Notification> findPendingScheduled(UUID companyId) {
        return repository.findPendingScheduled(companyId, Instant.now());
    }

    public Notification updateStatus(UUID notificationId, UUID companyId, NotificationStatus status) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setStatus(status);
        notification.setUpdatedAt(Instant.now());

        if (status == NotificationStatus.DELIVERED && notification.getDeliveredAt() == null) {
            notification.setDeliveredAt(Instant.now());
        } else if (status == NotificationStatus.READ && notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
        }

        return repository.save(notification);
    }

    public void delete(UUID notificationId, UUID companyId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setDeletedAt(Instant.now());
        repository.save(notification);
    }
}
