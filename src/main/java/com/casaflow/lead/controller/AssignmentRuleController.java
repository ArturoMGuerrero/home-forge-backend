package com.casaflow.lead.controller;

import com.casaflow.lead.domain.LeadAssignmentRule;
import com.casaflow.lead.dto.CreateAssignmentRuleRequest;
import com.casaflow.lead.dto.UpdateAssignmentRuleRequest;
import com.casaflow.lead.service.AssignmentRuleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignment-rules")
public class AssignmentRuleController {
    private final AssignmentRuleService service;

    public AssignmentRuleController(AssignmentRuleService service) {
        this.service = service;
    }

    @PostMapping
    public LeadAssignmentRule create(@Valid @RequestBody CreateAssignmentRuleRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<LeadAssignmentRule> list(@RequestParam UUID companyId) {
        return service.listByCompany(companyId);
    }

    @PatchMapping("/{ruleId}")
    public LeadAssignmentRule update(
        @PathVariable UUID ruleId,
        @Valid @RequestBody UpdateAssignmentRuleRequest request
    ) {
        return service.update(ruleId, request);
    }

    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable UUID ruleId, @RequestParam UUID companyId) {
        service.delete(ruleId, companyId);
    }
}
