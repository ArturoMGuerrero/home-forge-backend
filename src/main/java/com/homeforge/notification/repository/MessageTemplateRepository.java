package com.homeforge.notification.repository;

import com.homeforge.notification.domain.MessageTemplate;
import com.homeforge.notification.domain.MessageTemplateCategory;
import com.homeforge.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, UUID> {
    List<MessageTemplate> findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(UUID companyId);

    List<MessageTemplate> findByCompanyIdAndActiveAndDeletedAtIsNull(UUID companyId, Boolean active);

    List<MessageTemplate> findByCompanyIdAndTemplateTypeAndDeletedAtIsNull(UUID companyId, NotificationType templateType);

    List<MessageTemplate> findByCompanyIdAndCategoryAndDeletedAtIsNull(UUID companyId, MessageTemplateCategory category);

    Optional<MessageTemplate> findByCompanyIdAndTemplateTypeAndIsDefaultAndDeletedAtIsNull(UUID companyId, NotificationType templateType, Boolean isDefault);
}
