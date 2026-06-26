package com.casaflow.lead.controller;

import com.casaflow.lead.domain.LeadScoreHistory;
import com.casaflow.lead.service.LeadScoringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadScoringController {
    private final LeadScoringService scoringService;

    public LeadScoringController(LeadScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/{leadId}/calculate-score")
    public Map<String, Integer> calculateScore(
        @PathVariable UUID leadId,
        @RequestParam UUID companyId
    ) {
        int score = scoringService.calculateScore(leadId, companyId);
        return Map.of("score", score);
    }

    @GetMapping("/{leadId}/score-history")
    public List<LeadScoreHistory> getScoreHistory(@PathVariable UUID leadId) {
        return scoringService.getScoreHistory(leadId);
    }
}
