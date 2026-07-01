package com.casaflow.appointment.repository;

import com.casaflow.appointment.domain.AgentAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgentAvailabilityRepository extends JpaRepository<AgentAvailability, UUID> {
    List<AgentAvailability> findByUserIdAndDeletedAtIsNullOrderByDayOfWeekAscStartTimeAsc(UUID userId);

    List<AgentAvailability> findByCompanyIdAndDeletedAtIsNullOrderByUserIdAscDayOfWeekAscStartTimeAsc(UUID companyId);

    List<AgentAvailability> findByUserIdAndDayOfWeekAndDeletedAtIsNull(UUID userId, Integer dayOfWeek);
}
