package com.casaflow.appointment.repository;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByCompanyIdAndDeletedAtIsNullOrderByStartTimeAsc(UUID companyId);

    List<Appointment> findByAssignedUserIdAndDeletedAtIsNullOrderByStartTimeAsc(UUID userId);

    List<Appointment> findByLeadIdAndDeletedAtIsNullOrderByStartTimeAsc(UUID leadId);

    @Query("SELECT a FROM Appointment a WHERE a.companyId = :companyId AND a.startTime >= :start AND a.startTime < :end AND a.deletedAt IS NULL ORDER BY a.startTime ASC")
    List<Appointment> findByDateRange(@Param("companyId") UUID companyId, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT a FROM Appointment a WHERE a.assignedUserId = :userId AND a.startTime >= :start AND a.startTime < :end AND a.deletedAt IS NULL ORDER BY a.startTime ASC")
    List<Appointment> findByUserAndDateRange(@Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

    List<Appointment> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, AppointmentStatus status);
}
