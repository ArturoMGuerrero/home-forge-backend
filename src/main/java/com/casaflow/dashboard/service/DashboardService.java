package com.casaflow.dashboard.service;

import com.casaflow.dashboard.dto.DashboardMetricsResponse;
import com.casaflow.dashboard.dto.DashboardMetricsResponse.DailyMetric;
import com.casaflow.lead.domain.Lead;
import com.casaflow.lead.domain.LeadStatus;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.property.domain.Property;
import com.casaflow.property.domain.PropertyStatus;
import com.casaflow.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final PropertyRepository propertyRepository;
    private final LeadRepository leadRepository;

    public DashboardService(PropertyRepository propertyRepository, LeadRepository leadRepository) {
        this.propertyRepository = propertyRepository;
        this.leadRepository = leadRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getMetrics(UUID companyId, Instant startDate, Instant endDate) {
        // Obtener todas las propiedades y leads de la compañía
        List<Property> allProperties = propertyRepository.findByCompanyIdAndDeletedAtIsNull(companyId);
        List<Lead> allLeads = leadRepository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtDesc(companyId);

        // Filtrar por rango de fechas (basado en createdAt)
        List<Property> properties = allProperties.stream()
                .filter(p -> p.getCreatedAt().isAfter(startDate) && p.getCreatedAt().isBefore(endDate))
                .toList();

        List<Lead> leads = allLeads.stream()
                .filter(l -> l.getCreatedAt().isAfter(startDate) && l.getCreatedAt().isBefore(endDate))
                .toList();

        // Métricas de propiedades
        int totalProperties = properties.size();
        List<Property> activeSalesList = properties.stream()
                .filter(p -> "SALE".equals(p.getListingType()) && isActiveStatus(p.getStatus()))
                .toList();
        List<Property> activeRentalsList = properties.stream()
                .filter(p -> "RENT".equals(p.getListingType()) && isActiveStatus(p.getStatus()))
                .toList();
        List<Property> soldList = properties.stream().filter(p -> p.getStatus() == PropertyStatus.SOLD).toList();
        List<Property> rentedList = properties.stream().filter(p -> p.getStatus() == PropertyStatus.RENTED).toList();

        BigDecimal saleInventory = sumPropertyValues(activeSalesList);
        BigDecimal soldValue = sumPropertyValues(soldList);
        BigDecimal monthlyRentInventory = sumPropertyValues(activeRentalsList);
        BigDecimal monthlyRentValue = sumPropertyValues(rentedList);

        // Métricas de leads
        int totalLeads = leads.size();
        List<Lead> openLeadsList = leads.stream().filter(this::isOpenLead).toList();
        List<Lead> closedLeadsList = leads.stream().filter(l -> l.getStatus() == LeadStatus.CLOSED).toList();
        List<Lead> lostLeadsList = leads.stream().filter(l -> l.getStatus() == LeadStatus.LOST).toList();
        List<Lead> highPriorityList = leads.stream()
                .filter(l -> "HIGH".equals(l.getPriority()) && isOpenLead(l))
                .toList();

        Map<String, Integer> leadsByStatus = leads.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getStatus().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // Seguimientos
        Instant now = Instant.now();
        int dueFollowUps = (int) leads.stream()
                .filter(l -> l.getNextFollowUpAt() != null && l.getNextFollowUpAt().isBefore(now) && isOpenLead(l))
                .count();
        int upcomingFollowUps = (int) leads.stream()
                .filter(l -> l.getNextFollowUpAt() != null && l.getNextFollowUpAt().isAfter(now) && isOpenLead(l))
                .count();

        // Métricas diarias
        Map<String, DailyMetric> dailyMetrics = buildDailyMetrics(properties, leads, startDate, endDate);

        // Tasas de conversión
        BigDecimal leadToClosedRate = totalLeads > 0
                ? BigDecimal.valueOf(closedLeadsList.size()).divide(BigDecimal.valueOf(totalLeads), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal propertyToSoldRate = totalProperties > 0
                ? BigDecimal.valueOf(soldList.size()).divide(BigDecimal.valueOf(totalProperties), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new DashboardMetricsResponse(
                totalProperties,
                activeSalesList.size(),
                activeRentalsList.size(),
                soldList.size(),
                rentedList.size(),
                saleInventory,
                soldValue,
                monthlyRentInventory,
                monthlyRentValue,
                totalLeads,
                openLeadsList.size(),
                closedLeadsList.size(),
                lostLeadsList.size(),
                highPriorityList.size(),
                leadsByStatus,
                dueFollowUps,
                upcomingFollowUps,
                dailyMetrics,
                leadToClosedRate,
                propertyToSoldRate,
                startDate,
                endDate
        );
    }

    private boolean isActiveStatus(PropertyStatus status) {
        return status == PropertyStatus.AVAILABLE ||
               status == PropertyStatus.RESERVED ||
               status == PropertyStatus.UNDER_CONTRACT;
    }

    private boolean isOpenLead(Lead lead) {
        return lead.getStatus() != LeadStatus.CLOSED && lead.getStatus() != LeadStatus.LOST;
    }

    private BigDecimal sumPropertyValues(List<Property> properties) {
        return properties.stream()
                .filter(p -> "MXN".equals(p.getCurrencyCode()))
                .map(Property::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, DailyMetric> buildDailyMetrics(
            List<Property> properties,
            List<Lead> leads,
            Instant startDate,
            Instant endDate
    ) {
        Map<String, DailyMetric> metrics = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDate start = startDate.atZone(zoneId).toLocalDate();
        LocalDate end = endDate.atZone(zoneId).toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(formatter);
            Instant dayStart = date.atStartOfDay(zoneId).toInstant();
            Instant dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant();

            int newLeads = (int) leads.stream()
                    .filter(l -> l.getCreatedAt().isAfter(dayStart) && l.getCreatedAt().isBefore(dayEnd))
                    .count();

            int newProperties = (int) properties.stream()
                    .filter(p -> p.getCreatedAt().isAfter(dayStart) && p.getCreatedAt().isBefore(dayEnd))
                    .count();

            int closedLeads = (int) leads.stream()
                    .filter(l -> l.getStatus() == LeadStatus.CLOSED)
                    .filter(l -> l.getUpdatedAt() != null && l.getUpdatedAt().isAfter(dayStart) && l.getUpdatedAt().isBefore(dayEnd))
                    .count();

            int soldProperties = (int) properties.stream()
                    .filter(p -> p.getStatus() == PropertyStatus.SOLD)
                    .filter(p -> p.getUpdatedAt() != null && p.getUpdatedAt().isAfter(dayStart) && p.getUpdatedAt().isBefore(dayEnd))
                    .count();

            BigDecimal salesValue = properties.stream()
                    .filter(p -> p.getStatus() == PropertyStatus.SOLD)
                    .filter(p -> p.getUpdatedAt() != null && p.getUpdatedAt().isAfter(dayStart) && p.getUpdatedAt().isBefore(dayEnd))
                    .filter(p -> "MXN".equals(p.getCurrencyCode()))
                    .map(Property::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            metrics.put(dateStr, new DailyMetric(dateStr, newLeads, newProperties, closedLeads, soldProperties, salesValue));
        }

        return metrics;
    }
}
