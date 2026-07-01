package com.casaflow.appointment.service;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.casaflow.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Appointment create(Appointment appointment) {
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> listByCompany(UUID companyId) {
        return appointmentRepository.findByCompanyIdAndDeletedAtIsNullOrderByStartTimeAsc(companyId);
    }

    public List<Appointment> listByDateRange(UUID companyId, Instant start, Instant end) {
        return appointmentRepository.findByDateRange(companyId, start, end);
    }

    public List<Appointment> listByUser(UUID userId) {
        return appointmentRepository.findByAssignedUserIdAndDeletedAtIsNullOrderByStartTimeAsc(userId);
    }

    public List<Appointment> listByUserAndDateRange(UUID userId, Instant start, Instant end) {
        return appointmentRepository.findByUserAndDateRange(userId, start, end);
    }

    public List<Appointment> listByLead(UUID leadId) {
        return appointmentRepository.findByLeadIdAndDeletedAtIsNullOrderByStartTimeAsc(leadId);
    }

    public List<Appointment> listByStatus(UUID companyId, AppointmentStatus status) {
        return appointmentRepository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, status);
    }

    public Appointment findById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public Appointment update(UUID id, Appointment updates) {
        Appointment existing = findById(id);
        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getAppointmentType() != null) existing.setAppointmentType(updates.getAppointmentType());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getStartTime() != null) existing.setStartTime(updates.getStartTime());
        if (updates.getEndTime() != null) existing.setEndTime(updates.getEndTime());
        if (updates.getTimezone() != null) existing.setTimezone(updates.getTimezone());
        if (updates.getAllDay() != null) existing.setAllDay(updates.getAllDay());
        if (updates.getAssignedUserId() != null) existing.setAssignedUserId(updates.getAssignedUserId());
        if (updates.getLeadId() != null) existing.setLeadId(updates.getLeadId());
        if (updates.getPropertyId() != null) existing.setPropertyId(updates.getPropertyId());
        if (updates.getLocationType() != null) existing.setLocationType(updates.getLocationType());
        if (updates.getLocationAddress() != null) existing.setLocationAddress(updates.getLocationAddress());
        if (updates.getVirtualMeetingUrl() != null) existing.setVirtualMeetingUrl(updates.getVirtualMeetingUrl());
        if (updates.getReminderMinutes() != null) existing.setReminderMinutes(updates.getReminderMinutes());
        if (updates.getNotes() != null) existing.setNotes(updates.getNotes());
        if (updates.getOutcome() != null) existing.setOutcome(updates.getOutcome());
        if (updates.getFollowUpRequired() != null) existing.setFollowUpRequired(updates.getFollowUpRequired());
        existing.setUpdatedAt(Instant.now());
        return appointmentRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        Appointment appointment = findById(id);
        appointment.setDeletedAt(Instant.now());
        appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment updateStatus(UUID id, AppointmentStatus status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        appointment.setUpdatedAt(Instant.now());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment markReminderSent(UUID id) {
        Appointment appointment = findById(id);
        appointment.setReminderSent(true);
        appointment.setReminderSentAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment syncWithGoogleCalendar(UUID id, String eventId) {
        Appointment appointment = findById(id);
        appointment.setGoogleCalendarEventId(eventId);
        appointment.setLastSyncedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());
        return appointmentRepository.save(appointment);
    }
}
