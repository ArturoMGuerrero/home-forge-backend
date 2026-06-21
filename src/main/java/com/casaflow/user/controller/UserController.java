package com.casaflow.user.controller;

import com.casaflow.user.dto.CreateCompanyUserRequest;
import com.casaflow.user.dto.UserListResponse;
import com.casaflow.user.dto.UserResponse;
import com.casaflow.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
