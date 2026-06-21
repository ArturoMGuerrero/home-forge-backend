package com.casaflow.agenda.controller;

import com.casaflow.agenda.domain.Appointment;
import com.casaflow.agenda.dto.AppointmentRequest;
import com.casaflow.agenda.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) { this.service = service; }

    @GetMapping
    public List<Appointment> list(@RequestParam UUID companyId) { return service.list(companyId); }

    @PostMapping
    public Appointment create(@Valid @RequestBody AppointmentRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable UUID id, @Valid @RequestBody AppointmentRequest request) {
        return service.update(id, request);
    }
}
