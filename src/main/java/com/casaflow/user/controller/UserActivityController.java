package com.casaflow.user.controller;

import com.casaflow.user.dto.UserActivityResponse;
import com.casaflow.user.repository.UserActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-activity")
public class UserActivityController {

    private final UserActivityRepository repository;

    public UserActivityController(UserActivityRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<UserActivityResponse> listByCompany(
            @RequestParam UUID companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable)
                .map(UserActivityResponse::from);
    }

    @GetMapping("/user/{userId}")
    public Page<UserActivityResponse> listByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(UserActivityResponse::from);
    }
}
