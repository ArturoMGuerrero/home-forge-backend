package com.homeforge.notification.controller;

import com.homeforge.notification.domain.Notification;
import com.homeforge.notification.domain.NotificationStatus;
import com.homeforge.notification.dto.CreateNotificationRequest;
import com.homeforge.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public Notification create(@Valid @RequestBody CreateNotificationRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Notification> list(@RequestParam UUID companyId, @RequestParam(required = false) String status) {
        if (status != null) {
            return service.listByStatus(companyId, NotificationStatus.valueOf(status));
        }
        return service.listByCompany(companyId);
    }

    @GetMapping("/lead/{leadId}")
    public List<Notification> listByLead(@PathVariable UUID leadId) {
        return service.listByLead(leadId);
    }

    @PostMapping("/{notificationId}/send")
    public Notification send(@PathVariable UUID notificationId, @RequestParam UUID companyId) {
        return service.send(notificationId, companyId);
    }

    @PatchMapping("/{notificationId}/status")
    public Notification updateStatus(
            @PathVariable UUID notificationId,
            @RequestParam UUID companyId,
            @RequestBody Map<String, String> body
    ) {
        NotificationStatus status = NotificationStatus.valueOf(body.get("status"));
        return service.updateStatus(notificationId, companyId, status);
    }

    @DeleteMapping("/{notificationId}")
    public void delete(@PathVariable UUID notificationId, @RequestParam UUID companyId) {
        service.delete(notificationId, companyId);
    }
}
