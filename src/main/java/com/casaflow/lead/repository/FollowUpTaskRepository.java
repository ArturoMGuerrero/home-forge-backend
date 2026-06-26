package com.casaflow.lead.repository;

import com.casaflow.lead.domain.FollowUpTask;
import com.casaflow.lead.domain.FollowUpTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowUpTaskRepository extends JpaRepository<FollowUpTask, UUID> {
    List<FollowUpTask> findByCompanyIdAndDeletedAtIsNullOrderByScheduledForAsc(UUID companyId);

    List<FollowUpTask> findByLeadIdAndCompanyIdAndDeletedAtIsNullOrderByScheduledForAsc(UUID leadId, UUID companyId);

    Optional<FollowUpTask> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);

    List<FollowUpTask> findByStatusAndScheduledForBeforeAndDeletedAtIsNull(
        FollowUpTaskStatus status,
        Instant scheduledFor
    );

    @Query("SELECT t FROM FollowUpTask t WHERE t.companyId = :companyId " +
           "AND t.status = :status AND t.deletedAt IS NULL ORDER BY t.scheduledFor ASC")
    List<FollowUpTask> findByCompanyIdAndStatus(
        @Param("companyId") UUID companyId,
        @Param("status") FollowUpTaskStatus status
    );

    @Query("SELECT t FROM FollowUpTask t WHERE t.assignedToUserId = :userId " +
           "AND t.companyId = :companyId AND t.deletedAt IS NULL " +
           "ORDER BY t.scheduledFor ASC")
    List<FollowUpTask> findByAssignedUser(
        @Param("userId") UUID userId,
        @Param("companyId") UUID companyId
    );
}
