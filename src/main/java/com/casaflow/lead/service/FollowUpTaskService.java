package com.casaflow.lead.service;

import com.casaflow.lead.domain.FollowUpTask;
import com.casaflow.lead.domain.FollowUpTaskStatus;
import com.casaflow.lead.dto.CreateFollowUpTaskRequest;
import com.casaflow.lead.dto.UpdateFollowUpTaskRequest;
import com.casaflow.lead.repository.FollowUpTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FollowUpTaskService {
    private final FollowUpTaskRepository repository;

    public FollowUpTaskService(FollowUpTaskRepository repository) {
        this.repository = repository;
    }

    public FollowUpTask create(CreateFollowUpTaskRequest r) {
        return repository.save(new FollowUpTask(
            r.companyId(),
            r.leadId(),
            r.title(),
            r.description(),
            r.taskType(),
            r.scheduledFor(),
            r.assignedToUserId(),
            r.priority()
        ));
    }

    public List<FollowUpTask> listByCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByScheduledForAsc(companyId);
    }

    public List<FollowUpTask> listByLead(UUID leadId, UUID companyId) {
        return repository.findByLeadIdAndCompanyIdAndDeletedAtIsNullOrderByScheduledForAsc(leadId, companyId);
    }

    public List<FollowUpTask> listByUser(UUID userId, UUID companyId) {
        return repository.findByAssignedUser(userId, companyId);
    }

    public List<FollowUpTask> listOverdue() {
        return repository.findByStatusAndScheduledForBeforeAndDeletedAtIsNull(
            FollowUpTaskStatus.PENDING,
            Instant.now()
        );
    }

    @Transactional
    public FollowUpTask update(UUID taskId, UpdateFollowUpTaskRequest r) {
        FollowUpTask task = repository.findByIdAndCompanyIdAndDeletedAtIsNull(taskId, r.companyId())
            .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        if (r.title() != null) {
            // No direct setter, would need to add one or use reflection
            // For now, we'll handle status updates
        }

        if (r.status() != null) {
            task.setStatus(r.status());
            if (r.status() == FollowUpTaskStatus.COMPLETED && task.getCompletedAt() == null) {
                task.setCompletedAt(Instant.now());
            }
        }

        if (r.scheduledFor() != null) {
            task.setScheduledFor(r.scheduledFor());
        }

        if (r.assignedToUserId() != null) {
            task.setAssignedToUserId(r.assignedToUserId());
        }

        return repository.save(task);
    }

    @Transactional
    public void delete(UUID taskId, UUID companyId) {
        FollowUpTask task = repository.findByIdAndCompanyIdAndDeletedAtIsNull(taskId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        task.softDelete();
        repository.save(task);
    }

    @Transactional
    public void markOverdueTasks() {
        List<FollowUpTask> overdue = listOverdue();
        for (FollowUpTask task : overdue) {
            task.setStatus(FollowUpTaskStatus.OVERDUE);
            repository.save(task);
        }
    }
}
