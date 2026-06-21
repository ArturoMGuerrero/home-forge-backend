package com.casaflow.subscription;

import com.casaflow.subscription.dto.ChangePlanRequest;
import com.casaflow.subscription.dto.SubscriptionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/subscription")
public class SubscriptionController {
    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public SubscriptionResponse get(@PathVariable UUID companyId) {
        return service.get(companyId);
    }

    @PutMapping
    public SubscriptionResponse changePlan(
            @PathVariable UUID companyId,
            @Valid @RequestBody ChangePlanRequest request
    ) {
        return service.changePlan(companyId, request.planCode());
    }
}
