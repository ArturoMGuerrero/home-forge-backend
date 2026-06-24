package com.casaflow.lead.controller;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.dto.CreateLeadRequest;
import com.casaflow.lead.dto.CreateLeadActivityRequest;
import com.casaflow.lead.dto.UpdateLeadRequest;
import com.casaflow.lead.domain.LeadActivity;
import com.casaflow.lead.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/leads")
public class LeadController {
  private final LeadService service;
  public LeadController(LeadService service){this.service=service;}
  @PostMapping public Lead create(@Valid @RequestBody CreateLeadRequest request){ return service.create(request); }
  @GetMapping public List<Lead> list(@RequestParam UUID companyId){ return service.byCompany(companyId); }
  @GetMapping("/{leadId}") public Lead get(@PathVariable UUID leadId, @RequestParam UUID companyId){ return service.get(leadId, companyId); }
  @PutMapping("/{leadId}") public Lead update(@PathVariable UUID leadId, @Valid @RequestBody UpdateLeadRequest request){ return service.update(leadId, request); }
  @GetMapping("/{leadId}/activities") public List<LeadActivity> activities(@PathVariable UUID leadId, @RequestParam UUID companyId){ return service.activities(leadId, companyId); }
  @PostMapping("/{leadId}/activities") public LeadActivity addActivity(@PathVariable UUID leadId, @Valid @RequestBody CreateLeadActivityRequest request){ return service.addActivity(leadId, request); }
  @DeleteMapping("/{leadId}/activities/{activityId}") public void deleteActivity(@PathVariable UUID leadId, @PathVariable UUID activityId, @RequestParam UUID companyId){ service.deleteActivity(leadId, activityId, companyId); }
}
