package com.casaflow.notification.controller;

import com.casaflow.notification.domain.MessageTemplate;
import com.casaflow.notification.domain.MessageTemplateCategory;
import com.casaflow.notification.domain.NotificationType;
import com.casaflow.notification.dto.CreateTemplateRequest;
import com.casaflow.notification.service.MessageTemplateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/message-templates")
public class MessageTemplateController {
    private final MessageTemplateService service;

    public MessageTemplateController(MessageTemplateService service) {
        this.service = service;
    }

    @PostMapping
    public MessageTemplate create(@Valid @RequestBody CreateTemplateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<MessageTemplate> list(@RequestParam UUID companyId) {
        return service.listByCompany(companyId);
    }

    @GetMapping("/{templateId}")
    public MessageTemplate get(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        return service.get(templateId, companyId);
    }

    @GetMapping("/by-type")
    public List<MessageTemplate> listByType(
            @RequestParam UUID companyId,
            @RequestParam NotificationType type
    ) {
        return service.listByType(companyId, type);
    }

    @GetMapping("/by-category")
    public List<MessageTemplate> listByCategory(
            @RequestParam UUID companyId,
            @RequestParam MessageTemplateCategory category
    ) {
        return service.listByCategory(companyId, category);
    }

    @PatchMapping("/{templateId}")
    public MessageTemplate update(
            @PathVariable UUID templateId,
            @RequestParam UUID companyId,
            @RequestBody Map<String, String> body
    ) {
        return service.update(templateId, companyId, body.get("content"));
    }

    @PatchMapping("/{templateId}/toggle-active")
    public MessageTemplate toggleActive(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        return service.toggleActive(templateId, companyId);
    }

    @DeleteMapping("/{templateId}")
    public void delete(@PathVariable UUID templateId, @RequestParam UUID companyId) {
        service.delete(templateId, companyId);
    }
}
