package com.homeforge.lead.controller;

import com.homeforge.lead.domain.LeadAssignmentRule;
import com.homeforge.lead.dto.CreateAssignmentRuleRequest;
import com.homeforge.lead.dto.UpdateAssignmentRuleRequest;
import com.homeforge.lead.service.AssignmentRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Asignación automática", description = "Reglas para repartir prospectos nuevos entre asesores.")
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
