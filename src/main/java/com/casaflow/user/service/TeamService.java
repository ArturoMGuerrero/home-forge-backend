package com.casaflow.user.service;

import com.casaflow.user.domain.*;
import com.casaflow.user.dto.CreateTeamRequest;
import com.casaflow.user.dto.TeamResponse;
import com.casaflow.user.repository.TeamRepository;
import com.casaflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserActivityService userActivityService;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            UserActivityService userActivityService
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userActivityService = userActivityService;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> list(UUID companyId) {
        return teamRepository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtAsc(companyId)
                .stream()
                .map(TeamResponse::from)
                .toList();
    }

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        if (teamRepository.existsByCompanyIdAndNameIgnoreCaseAndDeletedAtIsNull(request.companyId(), request.name())) {
            throw new IllegalArgumentException("Ya existe un equipo con ese nombre.");
        }

        Team team = new Team(
                request.companyId(),
                request.name(),
                request.description(),
                request.leaderId()
        );

        if (request.memberIds() != null && !request.memberIds().isEmpty()) {
            List<User> members = userRepository.findAllById(request.memberIds());
            members.forEach(team::addMember);
        }

        Team saved = teamRepository.save(team);

        userActivityService.log(
                request.companyId(),
                request.requesterUserId(),
                ActivityType.TEAM_CREATED,
                ActivityCategory.TEAM_MANAGEMENT,
                "Team",
                saved.getId(),
                "Team created: " + saved.getName(),
                "Equipo creado: " + saved.getName()
        );

        return TeamResponse.from(saved);
    }

    @Transactional
    public void addMember(UUID teamId, UUID companyId, UUID userId, UUID requesterId) {
        Team team = teamRepository.findByIdAndCompanyIdAndDeletedAtIsNull(teamId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado."));
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        team.addMember(user);
        teamRepository.save(team);

        userActivityService.log(
                companyId,
                requesterId,
                ActivityType.TEAM_MEMBER_ADDED,
                ActivityCategory.TEAM_MANAGEMENT,
                "Team",
                teamId,
                "Member added to team: " + user.getFullName(),
                "Miembro agregado al equipo: " + user.getFullName()
        );
    }

    @Transactional
    public void removeMember(UUID teamId, UUID companyId, UUID userId, UUID requesterId) {
        Team team = teamRepository.findByIdAndCompanyIdAndDeletedAtIsNull(teamId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado."));
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        team.removeMember(user);
        teamRepository.save(team);

        userActivityService.log(
                companyId,
                requesterId,
                ActivityType.TEAM_MEMBER_REMOVED,
                ActivityCategory.TEAM_MANAGEMENT,
                "Team",
                teamId,
                "Member removed from team: " + user.getFullName(),
                "Miembro removido del equipo: " + user.getFullName()
        );
    }
}
