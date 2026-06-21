package com.casaflow.agenda.repository;

import com.casaflow.agenda.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByCompanyIdAndDeletedAtIsNullOrderByStartsAtAsc(UUID companyId);
    Optional<Appointment> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
