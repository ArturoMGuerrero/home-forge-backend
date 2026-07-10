package com.casaflow.appointment.controller;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.casaflow.appointment.dto.AppointmentResponse;
import com.casaflow.appointment.dto.CreateAppointmentRequest;
import com.casaflow.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setCompanyId(request.companyId());
        appointment.setTitle(request.title());
        appointment.setDescription(request.description());
        appointment.setAppointmentType(request.appointmentType());
        appointment.setStatus(request.status() != null ? request.status() : AppointmentStatus.SCHEDULED);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setTimezone(request.timezone() != null ? request.timezone() : "America/Mexico_City");
        appointment.setAllDay(request.allDay() != null ? request.allDay() : false);
        appointment.setAssignedUserId(request.assignedUserId());
        appointment.setLeadId(request.leadId());
        appointment.setPropertyId(request.propertyId());
        appointment.setLocationAddress(request.locationAddress());
        appointment.setVirtualMeetingUrl(request.virtualMeetingUrl());
        appointment.setReminderMinutes(request.reminderMinutes());
        appointment.setNotes(request.notes());
        appointment.setCreatedByUserId(request.createdByUserId());

        Appointment created = appointmentService.create(appointment);
        return ResponseEntity.ok(AppointmentResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> list(
            @RequestParam UUID companyId,
            @RequestParam(required = false) Long startTimestamp,
            @RequestParam(required = false) Long endTimestamp,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID leadId,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        List<Appointment> appointments;
        if (leadId != null) {
            appointments = appointmentService.listByLead(leadId);
        } else if (userId != null && startTimestamp != null && endTimestamp != null) {
            Instant start = Instant.ofEpochMilli(startTimestamp);
            Instant end = Instant.ofEpochMilli(endTimestamp);
            appointments = appointmentService.listByUserAndDateRange(userId, start, end);
        } else if (userId != null) {
            appointments = appointmentService.listByUser(userId);
        } else if (startTimestamp != null && endTimestamp != null) {
            Instant start = Instant.ofEpochMilli(startTimestamp);
            Instant end = Instant.ofEpochMilli(endTimestamp);
            appointments = appointmentService.listByDateRange(companyId, start, end);
        } else if (status != null) {
            appointments = appointmentService.listByStatus(companyId, status);
        } else {
            appointments = appointmentService.listByCompany(companyId);
        }

        return ResponseEntity.ok(
            appointments.stream()
                .map(AppointmentResponse::from)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> get(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable UUID id, @RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.update(id, appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        AppointmentStatus status = AppointmentStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @PostMapping("/{id}/reminder-sent")
    public ResponseEntity<Appointment> markReminderSent(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.markReminderSent(id));
    }

    @PostMapping("/{id}/sync-google")
    public ResponseEntity<Appointment> syncWithGoogle(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String eventId = body.get("eventId");
        return ResponseEntity.ok(appointmentService.syncWithGoogleCalendar(id, eventId));
    }
}
