package com.homeforge.lead.controller;

import com.homeforge.lead.domain.FollowUpTask;
import com.homeforge.lead.dto.CreateFollowUpTaskRequest;
import com.homeforge.lead.dto.UpdateFollowUpTaskRequest;
import com.homeforge.lead.service.FollowUpTaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/follow-up-tasks")
public class FollowUpTaskController {
    private final FollowUpTaskService service;

    public FollowUpTaskController(FollowUpTaskService service) {
        this.service = service;
    }

    @PostMapping
    public FollowUpTask create(@Valid @RequestBody CreateFollowUpTaskRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<FollowUpTask> list(@RequestParam UUID companyId) {
        return service.listByCompany(companyId);
    }

    @GetMapping("/lead/{leadId}")
    public List<FollowUpTask> listByLead(@PathVariable UUID leadId, @RequestParam UUID companyId) {
        return service.listByLead(leadId, companyId);
    }

    @GetMapping("/user/{userId}")
    public List<FollowUpTask> listByUser(@PathVariable UUID userId, @RequestParam UUID companyId) {
        return service.listByUser(userId, companyId);
    }

    @GetMapping("/overdue")
    public List<FollowUpTask> listOverdue() {
        return service.listOverdue();
    }

    @PatchMapping("/{taskId}")
    public FollowUpTask update(@PathVariable UUID taskId, @Valid @RequestBody UpdateFollowUpTaskRequest request) {
        return service.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public void delete(@PathVariable UUID taskId, @RequestParam UUID companyId) {
        service.delete(taskId, companyId);
    }
}
