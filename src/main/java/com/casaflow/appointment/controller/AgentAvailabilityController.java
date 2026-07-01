package com.casaflow.appointment.controller;

import com.casaflow.appointment.domain.AgentAvailability;
import com.casaflow.appointment.service.AgentAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agent-availability")
public class AgentAvailabilityController {
    private final AgentAvailabilityService availabilityService;

    public AgentAvailabilityController(AgentAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<AgentAvailability> create(@RequestBody AgentAvailability availability) {
        return ResponseEntity.ok(availabilityService.create(availability));
    }

    @GetMapping
    public ResponseEntity<List<AgentAvailability>> list(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) Integer dayOfWeek
    ) {
        if (userId != null && dayOfWeek != null) {
            return ResponseEntity.ok(availabilityService.listByUserAndDay(userId, dayOfWeek));
        }
        if (userId != null) {
            return ResponseEntity.ok(availabilityService.listByUser(userId));
        }
        if (companyId != null) {
            return ResponseEntity.ok(availabilityService.listByCompany(companyId));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentAvailability> get(@PathVariable UUID id) {
        return ResponseEntity.ok(availabilityService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentAvailability> update(@PathVariable UUID id, @RequestBody AgentAvailability availability) {
        return ResponseEntity.ok(availabilityService.update(id, availability));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        availabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
