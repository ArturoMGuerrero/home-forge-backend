package com.casaflow.user.controller;

import com.casaflow.user.dto.CreateTeamRequest;
import com.casaflow.user.dto.TeamResponse;
import com.casaflow.user.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamResponse> list(@RequestParam UUID companyId) {
        return service.list(companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse create(@Valid @RequestBody CreateTeamRequest request) {
        return service.create(request);
    }

    @PostMapping("/{teamId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @RequestParam UUID companyId,
            @RequestParam UUID requesterId
    ) {
        service.addMember(teamId, companyId, userId, requesterId);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @RequestParam UUID companyId,
            @RequestParam UUID requesterId
    ) {
        service.removeMember(teamId, companyId, userId, requesterId);
    }
}
