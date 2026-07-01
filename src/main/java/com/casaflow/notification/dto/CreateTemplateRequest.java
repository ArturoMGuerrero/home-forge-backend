package com.casaflow.notification.dto;

import com.casaflow.notification.domain.MessageTemplateCategory;
import com.casaflow.notification.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateTemplateRequest {
    @NotNull
    private UUID companyId;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private NotificationType templateType;
    @NotNull
    private NotificationType channel;
    private String subject;
    @NotBlank
    private String content;
    private String variables;
    private MessageTemplateCategory category;
    private Boolean isDefault = false;

    // Getters and Setters
    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotificationType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(NotificationType templateType) {
        this.templateType = templateType;
    }

    public NotificationType getChannel() {
        return channel;
    }

    public void setChannel(NotificationType channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public MessageTemplateCategory getCategory() {
        return category;
    }

    public void setCategory(MessageTemplateCategory category) {
        this.category = category;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
