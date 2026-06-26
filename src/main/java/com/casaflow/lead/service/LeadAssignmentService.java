package com.casaflow.lead.service;

import com.casaflow.lead.domain.*;
import com.casaflow.lead.repository.LeadAssignmentRuleRepository;
import com.casaflow.user.domain.User;
import com.casaflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeadAssignmentService {
    private final LeadAssignmentRuleRepository ruleRepository;
    private final UserRepository userRepository;

    public LeadAssignmentService(
        LeadAssignmentRuleRepository ruleRepository,
        UserRepository userRepository
    ) {
        this.ruleRepository = ruleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UUID assignLead(Lead lead) {
        List<LeadAssignmentRule> rules = ruleRepository.findActiveRules(lead.getCompanyId());

        for (LeadAssignmentRule rule : rules) {
            if (matchesCriteria(lead, rule)) {
                UUID assignedUserId = selectUser(rule, lead.getCompanyId());
                if (assignedUserId != null) {
                    return assignedUserId;
                }
            }
        }

        return null;
    }

    private boolean matchesCriteria(Lead lead, LeadAssignmentRule rule) {
        if (rule.getCriteriaSource() != null && !rule.getCriteriaSource().equals(lead.getSource())) {
            return false;
        }

        if (rule.getCriteriaListingType() != null && !rule.getCriteriaListingType().equals(lead.getListingType())) {
            return false;
        }

        if (rule.getCriteriaCity() != null && !rule.getCriteriaCity().equalsIgnoreCase(lead.getCity())) {
            return false;
        }

        if (rule.getCriteriaPropertyType() != null && !rule.getCriteriaPropertyType().equals(lead.getPropertyType())) {
            return false;
        }

        if (rule.getCriteriaBudgetMin() != null && lead.getBudgetMax() != null) {
            if (lead.getBudgetMax().compareTo(rule.getCriteriaBudgetMin()) < 0) {
                return false;
            }
        }

        if (rule.getCriteriaBudgetMax() != null && lead.getBudgetMin() != null) {
            if (lead.getBudgetMin().compareTo(rule.getCriteriaBudgetMax()) > 0) {
                return false;
            }
        }

        return true;
    }

    private UUID selectUser(LeadAssignmentRule rule, UUID companyId) {
        String userIdsStr = rule.getAssignedUserIds();
        if (userIdsStr == null || userIdsStr.isBlank()) {
            return null;
        }

        List<UUID> userIds = Arrays.stream(userIdsStr.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(UUID::fromString)
            .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return null;
        }

        List<User> users = userRepository.findAllById(userIds).stream()
            .filter(u -> u.getCompanyId().equals(companyId) && u.getDeletedAt() == null)
            .collect(Collectors.toList());

        if (users.isEmpty()) {
            return null;
        }

        return switch (rule.getAssignmentStrategy()) {
            case ROUND_ROBIN -> selectRoundRobin(users);
            case LEAST_ASSIGNED -> selectLeastAssigned(users);
            case RANDOM -> selectRandom(users);
            case HIGHEST_SCORE -> users.get(0).getId(); // For now, just return first
        };
    }

    private UUID selectRoundRobin(List<User> users) {
        User selected = users.stream()
            .min(Comparator.comparing(u ->
                u.getLastAssignedLeadAt() != null ? u.getLastAssignedLeadAt() : Instant.MIN
            ))
            .orElse(users.get(0));

        selected.setLastAssignedLeadAt(Instant.now());
        userRepository.save(selected);

        return selected.getId();
    }

    private UUID selectLeastAssigned(List<User> users) {
        // For now, same as round robin
        // In a real implementation, you'd count leads per user
        return selectRoundRobin(users);
    }

    private UUID selectRandom(List<User> users) {
        int index = new Random().nextInt(users.size());
        User selected = users.get(index);

        selected.setLastAssignedLeadAt(Instant.now());
        userRepository.save(selected);

        return selected.getId();
    }
}
