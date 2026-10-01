package com.homeforge.dashboard.controller;

import com.homeforge.dashboard.dto.DashboardMetricsResponse;
import com.homeforge.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public DashboardMetricsResponse getMetrics(
            @RequestParam UUID companyId,
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate,
            @RequestParam(required = false, defaultValue = "30") Integer days
    ) {
        Instant end = endDate != null ? Instant.ofEpochMilli(endDate) : Instant.now();
        Instant start = startDate != null ? Instant.ofEpochMilli(startDate) : end.minus(days, ChronoUnit.DAYS);

        return dashboardService.getMetrics(companyId, start, end);
    }
}
