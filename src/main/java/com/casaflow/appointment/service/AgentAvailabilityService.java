package com.casaflow.appointment.service;

import com.casaflow.appointment.domain.AgentAvailability;
import com.casaflow.appointment.repository.AgentAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AgentAvailabilityService {
    private final AgentAvailabilityRepository availabilityRepository;

    public AgentAvailabilityService(AgentAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public AgentAvailability create(AgentAvailability availability) {
        availability.setCreatedAt(Instant.now());
        availability.setUpdatedAt(Instant.now());
        return availabilityRepository.save(availability);
    }

    public List<AgentAvailability> listByUser(UUID userId) {
        return availabilityRepository.findByUserIdAndDeletedAtIsNullOrderByDayOfWeekAscStartTimeAsc(userId);
    }

    public List<AgentAvailability> listByCompany(UUID companyId) {
        return availabilityRepository.findByCompanyIdAndDeletedAtIsNullOrderByUserIdAscDayOfWeekAscStartTimeAsc(companyId);
    }

    public List<AgentAvailability> listByUserAndDay(UUID userId, Integer dayOfWeek) {
        return availabilityRepository.findByUserIdAndDayOfWeekAndDeletedAtIsNull(userId, dayOfWeek);
    }

    public AgentAvailability findById(UUID id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
    }

    @Transactional
    public AgentAvailability update(UUID id, AgentAvailability updates) {
        AgentAvailability existing = findById(id);
        if (updates.getDayOfWeek() != null) existing.setDayOfWeek(updates.getDayOfWeek());
        if (updates.getStartTime() != null) existing.setStartTime(updates.getStartTime());
        if (updates.getEndTime() != null) existing.setEndTime(updates.getEndTime());
        if (updates.getTimezone() != null) existing.setTimezone(updates.getTimezone());
        if (updates.getIsAvailable() != null) existing.setIsAvailable(updates.getIsAvailable());
        existing.setUpdatedAt(Instant.now());
        return availabilityRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        AgentAvailability availability = findById(id);
        availability.setDeletedAt(Instant.now());
        availabilityRepository.save(availability);
    }
}
