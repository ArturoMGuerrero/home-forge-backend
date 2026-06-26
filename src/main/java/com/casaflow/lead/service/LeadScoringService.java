package com.casaflow.lead.service;

import com.casaflow.lead.domain.*;
import com.casaflow.lead.repository.LeadActivityRepository;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.lead.repository.LeadScoreHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class LeadScoringService {
    private final LeadRepository leadRepository;
    private final LeadActivityRepository activityRepository;
    private final LeadScoreHistoryRepository scoreHistoryRepository;

    public LeadScoringService(
        LeadRepository leadRepository,
        LeadActivityRepository activityRepository,
        LeadScoreHistoryRepository scoreHistoryRepository
    ) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.scoreHistoryRepository = scoreHistoryRepository;
    }

    @Transactional
    public int calculateScore(UUID leadId, UUID companyId) {
        Lead lead = leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(leadId, companyId)
            .orElseThrow(() -> new IllegalArgumentException("Lead no encontrado"));

        int score = 0;
        StringBuilder reasons = new StringBuilder();

        // 1. Status del lead (0-30 puntos)
        score += getStatusScore(lead.getStatus());
        reasons.append("Status: ").append(lead.getStatus()).append("; ");

        // 2. Prioridad (0-15 puntos)
        score += getPriorityScore(lead.getPriority());
        reasons.append("Prioridad: ").append(lead.getPriority()).append("; ");

        // 3. Información completa (0-20 puntos)
        int completenessScore = 0;
        if (lead.getEmail() != null) completenessScore += 5;
        if (lead.getPhoneE164() != null) completenessScore += 5;
        if (lead.getBudgetMax() != null) completenessScore += 5;
        if (lead.getCity() != null) completenessScore += 3;
        if (lead.getPropertyType() != null) completenessScore += 2;
        score += completenessScore;
        reasons.append("Completitud: ").append(completenessScore).append("; ");

        // 4. Presupuesto (0-15 puntos)
        if (lead.getBudgetMax() != null) {
            int budgetScore = getBudgetScore(lead.getBudgetMax());
            score += budgetScore;
            reasons.append("Presupuesto: ").append(budgetScore).append("; ");
        }

        // 5. Actividad reciente (0-20 puntos)
        List<LeadActivity> recentActivities = activityRepository
            .findByLeadIdAndCompanyIdOrderByOccurredAtDesc(leadId, companyId)
            .stream()
            .filter(a -> a.getOccurredAt().isAfter(Instant.now().minus(30, ChronoUnit.DAYS)))
            .toList();

        int activityScore = Math.min(20, recentActivities.size() * 4);
        score += activityScore;
        reasons.append("Actividades (30d): ").append(recentActivities.size()).append("; ");

        // 6. Antigüedad del lead (penalización 0-10 puntos)
        long daysOld = ChronoUnit.DAYS.between(lead.getCreatedAt(), Instant.now());
        int ageScore = 0;
        if (daysOld > 90) ageScore = -10;
        else if (daysOld > 60) ageScore = -5;
        else if (daysOld > 30) ageScore = -2;
        score += ageScore;
        if (ageScore < 0) {
            reasons.append("Antigüedad: ").append(daysOld).append(" días (").append(ageScore).append("); ");
        }

        // Asegurar que el score esté entre 0 y 100
        score = Math.max(0, Math.min(100, score));

        // Actualizar score si cambió
        if (lead.getScore() != score) {
            int oldScore = lead.getScore();
            lead.setScore(score);
            leadRepository.save(lead);

            // Guardar historial
            scoreHistoryRepository.save(new LeadScoreHistory(
                companyId,
                leadId,
                oldScore,
                score,
                reasons.toString()
            ));
        }

        return score;
    }

    private int getStatusScore(LeadStatus status) {
        return switch (status) {
            case NEW -> 10;
            case CONTACTED -> 15;
            case QUALIFIED -> 25;
            case TOUR_SCHEDULED -> 30;
            case TOUR_COMPLETED -> 28;
            case OFFER_MADE -> 30;
            case UNDER_CONTRACT -> 30;
            case CLOSED -> 5;
            case LOST -> 0;
        };
    }

    private int getPriorityScore(LeadPriority priority) {
        return switch (priority) {
            case LOW -> 5;
            case MEDIUM -> 10;
            case HIGH -> 15;
        };
    }

    private int getBudgetScore(BigDecimal budget) {
        double amount = budget.doubleValue();
        if (amount >= 5000000) return 15;      // 5M+ MXN
        if (amount >= 2000000) return 12;      // 2-5M MXN
        if (amount >= 1000000) return 10;      // 1-2M MXN
        if (amount >= 500000) return 7;        // 500K-1M MXN
        if (amount >= 100000) return 5;        // 100K-500K MXN
        return 2;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void recalculateAllScores() {
        List<Lead> allLeads = leadRepository.findAll()
            .stream()
            .filter(l -> l.getDeletedAt() == null)
            .toList();

        for (Lead lead : allLeads) {
            try {
                calculateScore(lead.getId(), lead.getCompanyId());
            } catch (Exception e) {
                // Log error pero continuar con el siguiente
                System.err.println("Error scoring lead " + lead.getId() + ": " + e.getMessage());
            }
        }
    }

    public List<LeadScoreHistory> getScoreHistory(UUID leadId) {
        return scoreHistoryRepository.findByLeadIdOrderByCreatedAtDesc(leadId);
    }
}
