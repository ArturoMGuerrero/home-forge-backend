package com.casaflow.match.controller;

import com.casaflow.match.dto.MatchRequest;
import com.casaflow.match.dto.MatchResponse;
import com.casaflow.match.service.LeadPropertyMatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/property-matches")
public class LeadPropertyMatchController {
    private final LeadPropertyMatchService service;

    public LeadPropertyMatchController(LeadPropertyMatchService service) { this.service = service; }

    @GetMapping
    public List<MatchResponse> list(@RequestParam UUID companyId) { return service.list(companyId); }

    @PostMapping
    public MatchResponse create(@Valid @RequestBody MatchRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public MatchResponse update(@PathVariable UUID id, @Valid @RequestBody MatchRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam UUID companyId) {
        service.delete(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
