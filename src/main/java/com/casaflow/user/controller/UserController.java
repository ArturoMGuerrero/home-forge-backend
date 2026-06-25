package com.casaflow.user.controller;

import com.casaflow.user.dto.*;
import com.casaflow.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public UserListResponse list(@RequestParam UUID companyId, @RequestParam UUID requesterUserId) {
        return service.list(companyId, requesterUserId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateCompanyUserRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(userId, request);
    }

    @PatchMapping("/{userId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeStatus(@PathVariable UUID userId, @Valid @RequestBody ChangeUserStatusRequest request) {
        service.changeStatus(userId, request);
    }

    @PatchMapping("/{userId}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeRole(@PathVariable UUID userId, @Valid @RequestBody ChangeUserRoleRequest request) {
        service.changeRole(userId, request);
    }

    @GetMapping("/{userId}/settings")
    public UserSettingsResponse getSettings(@PathVariable UUID userId) {
        return service.getSettings(userId);
    }

    @PatchMapping("/{userId}/settings")
    public UserSettingsResponse updateSettings(@PathVariable UUID userId, @Valid @RequestBody UpdateUserSettingsRequest request) {
        return service.updateSettings(userId, request);
    }
}
