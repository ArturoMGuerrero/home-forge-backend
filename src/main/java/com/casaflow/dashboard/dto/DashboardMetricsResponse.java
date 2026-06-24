package com.casaflow.dashboard.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record DashboardMetricsResponse(
        // Métricas de propiedades
        int totalProperties,
        int activeSales,
        int activeRentals,
        int soldProperties,
        int rentedProperties,
        BigDecimal saleInventoryValue,
        BigDecimal soldValue,
        BigDecimal monthlyRentInventory,
        BigDecimal monthlyRentValue,

        // Métricas de leads
        int totalLeads,
        int openLeads,
        int closedLeads,
        int lostLeads,
        int highPriorityLeads,
        Map<String, Integer> leadsByStatus,

        // Métricas de seguimiento
        int dueFollowUps,
        int upcomingFollowUps,

        // Métricas de tiempo (para gráficas)
        Map<String, DailyMetric> dailyMetrics,

        // Tasas de conversión
        BigDecimal leadToClosedRate,
        BigDecimal propertyToSoldRate,

        // Período consultado
        Instant startDate,
        Instant endDate
) {
    public record DailyMetric(
            String date,
            int newLeads,
            int newProperties,
            int closedLeads,
            int soldProperties,
            BigDecimal salesValue
    ) {}
}
