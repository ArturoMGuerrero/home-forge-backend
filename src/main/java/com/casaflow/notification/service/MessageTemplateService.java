package com.casaflow.notification.service;

import com.casaflow.notification.domain.MessageTemplate;
import com.casaflow.notification.domain.MessageTemplateCategory;
import com.casaflow.notification.domain.NotificationType;
import com.casaflow.notification.dto.CreateTemplateRequest;
import com.casaflow.notification.repository.MessageTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class MessageTemplateService {
    private final MessageTemplateRepository repository;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    public MessageTemplateService(MessageTemplateRepository repository) {
        this.repository = repository;
    }

    public MessageTemplate create(CreateTemplateRequest request) {
        // Si es default, quitar el flag de otros templates del mismo tipo
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            repository.findByCompanyIdAndTemplateTypeAndIsDefaultAndDeletedAtIsNull(
                    request.getCompanyId(), request.getTemplateType(), true
            ).ifPresent(existing -> {
                existing.setIsDefault(false);
                repository.save(existing);
            });
        }

        MessageTemplate template = new MessageTemplate();
        template.setCompanyId(request.getCompanyId());
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setTemplateType(request.getTemplateType());
        template.setChannel(request.getChannel());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setVariables(request.getVariables());
        template.setCategory(request.getCategory());
        template.setIsDefault(request.getIsDefault());
        template.setActive(true);

        return repository.save(template);
    }

    public List<MessageTemplate> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(companyId);
    }

    public MessageTemplate get(UUID templateId, UUID companyId) {
        MessageTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        return template;
    }

    public List<MessageTemplate> listByType(UUID companyId, NotificationType type) {
        return repository.findByCompanyIdAndTemplateTypeAndDeletedAtIsNull(companyId, type);
    }

    public List<MessageTemplate> listByCategory(UUID companyId, MessageTemplateCategory category) {
        return repository.findByCompanyIdAndCategoryAndDeletedAtIsNull(companyId, category);
    }

    public MessageTemplate update(UUID templateId, UUID companyId, String content) {
        MessageTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        template.setContent(content);
        template.setUpdatedAt(Instant.now());
        return repository.save(template);
    }

    public MessageTemplate toggleActive(UUID templateId, UUID companyId) {
        MessageTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        template.setActive(!template.getActive());
        template.setUpdatedAt(Instant.now());
        return repository.save(template);
    }

    public String renderTemplate(String content, Map<String, String> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1).trim();
            String replacement = variables.getOrDefault(varName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public void delete(UUID templateId, UUID companyId) {
        MessageTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!template.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Unauthorized");
        }

        template.setDeletedAt(Instant.now());
        repository.save(template);
    }
}
