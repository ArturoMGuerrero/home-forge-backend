package com.casaflow.notification.repository;

import com.casaflow.notification.domain.Notification;
import com.casaflow.notification.domain.NotificationStatus;
import com.casaflow.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID companyId);

    List<Notification> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, NotificationStatus status);

    List<Notification> findByCompanyIdAndNotificationTypeAndDeletedAtIsNull(UUID companyId, NotificationType type);

    List<Notification> findByRecipientIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID recipientId);

    List<Notification> findByLeadIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID leadId);

    @Query("SELECT n FROM Notification n WHERE n.companyId = :companyId AND n.status = 'PENDING' AND n.scheduledFor <= :now AND n.deletedAt IS NULL ORDER BY n.priority DESC, n.scheduledFor ASC")
    List<Notification> findPendingScheduled(@Param("companyId") UUID companyId, @Param("now") Instant now);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.companyId = :companyId AND n.status = :status AND n.deletedAt IS NULL")
    long countByStatus(@Param("companyId") UUID companyId, @Param("status") NotificationStatus status);
}
