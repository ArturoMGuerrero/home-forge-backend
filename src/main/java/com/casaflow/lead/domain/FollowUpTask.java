package com.casaflow.lead.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "follow_up_tasks")
public class FollowUpTask extends AuditableEntity {
    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID leadId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowUpTaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowUpTaskStatus status = FollowUpTaskStatus.PENDING;

    @Column(nullable = false)
    private Instant scheduledFor;

    private Instant completedAt;

    private UUID assignedToUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowUpTaskPriority priority = FollowUpTaskPriority.MEDIUM;

    private String reminderSentAt;

    protected FollowUpTask() {}

    public FollowUpTask(
            UUID companyId,
            UUID leadId,
            String title,
            String description,
            FollowUpTaskType taskType,
            Instant scheduledFor,
            UUID assignedToUserId,
            FollowUpTaskPriority priority
    ) {
        this.companyId = companyId;
        this.leadId = leadId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.scheduledFor = scheduledFor;
        this.assignedToUserId = assignedToUserId;
        this.priority = priority != null ? priority : FollowUpTaskPriority.MEDIUM;
    }

    public UUID getCompanyId() { return companyId; }
    public UUID getLeadId() { return leadId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public FollowUpTaskType getTaskType() { return taskType; }
    public FollowUpTaskStatus getStatus() { return status; }
    public Instant getScheduledFor() { return scheduledFor; }
    public Instant getCompletedAt() { return completedAt; }
    public UUID getAssignedToUserId() { return assignedToUserId; }
    public FollowUpTaskPriority getPriority() { return priority; }
    public String getReminderSentAt() { return reminderSentAt; }

    public void setStatus(FollowUpTaskStatus status) { this.status = status; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public void setReminderSentAt(String reminderSentAt) { this.reminderSentAt = reminderSentAt; }
    public void setScheduledFor(Instant scheduledFor) { this.scheduledFor = scheduledFor; }
    public void setAssignedToUserId(UUID assignedToUserId) { this.assignedToUserId = assignedToUserId; }
}
