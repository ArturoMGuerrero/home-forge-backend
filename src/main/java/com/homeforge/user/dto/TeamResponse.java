package com.homeforge.user.dto;

import com.homeforge.user.domain.Team;

import java.util.List;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String description,
        UUID leaderId,
        boolean active,
        int memberCount,
        List<UUID> memberIds
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getLeaderId(),
                team.isActive(),
                team.getMembers().size(),
                team.getMembers().stream().map(u -> u.getId()).toList()
        );
    }
}
