package com.homeforge.lead.controller;

import com.homeforge.lead.domain.LeadScoreHistory;
import com.homeforge.lead.service.LeadScoringService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Puntuación de prospectos", description = "Calificación automática de qué tan probable es cerrar con cada prospecto.")
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
