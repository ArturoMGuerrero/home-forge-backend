package com.casaflow.appointment.controller;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.casaflow.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.create(appointment));
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> list(
            @RequestParam UUID companyId,
            @RequestParam(required = false) Long startTimestamp,
            @RequestParam(required = false) Long endTimestamp,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID leadId,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        if (leadId != null) {
            return ResponseEntity.ok(appointmentService.listByLead(leadId));
        }
        if (userId != null && startTimestamp != null && endTimestamp != null) {
            Instant start = Instant.ofEpochMilli(startTimestamp);
            Instant end = Instant.ofEpochMilli(endTimestamp);
            return ResponseEntity.ok(appointmentService.listByUserAndDateRange(userId, start, end));
        }
        if (userId != null) {
            return ResponseEntity.ok(appointmentService.listByUser(userId));
        }
        if (startTimestamp != null && endTimestamp != null) {
            Instant start = Instant.ofEpochMilli(startTimestamp);
            Instant end = Instant.ofEpochMilli(endTimestamp);
            return ResponseEntity.ok(appointmentService.listByDateRange(companyId, start, end));
        }
        if (status != null) {
            return ResponseEntity.ok(appointmentService.listByStatus(companyId, status));
        }
        return ResponseEntity.ok(appointmentService.listByCompany(companyId));
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
