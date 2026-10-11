package com.homeforge.subscription;

import com.homeforge.subscription.dto.SubscriptionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Suscripción", description = "Plan actual, límites y estado de la suscripción de la empresa.")
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

}
