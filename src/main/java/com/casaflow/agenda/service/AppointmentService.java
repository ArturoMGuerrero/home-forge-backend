package com.casaflow.agenda.service;

import com.casaflow.agenda.domain.Appointment;
import com.casaflow.agenda.dto.AppointmentRequest;
import com.casaflow.agenda.repository.AppointmentRepository;
import com.casaflow.lead.repository.LeadRepository;
import com.casaflow.property.repository.PropertyRepository;
import com.casaflow.subscription.SubscriptionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;
    private final LeadRepository leadRepository;
    private final PropertyRepository propertyRepository;
    private final SubscriptionValidator subscriptionValidator;

    public AppointmentService(AppointmentRepository repository, LeadRepository leadRepository, PropertyRepository propertyRepository, SubscriptionValidator subscriptionValidator) {
        this.repository = repository;
        this.leadRepository = leadRepository;
        this.propertyRepository = propertyRepository;
        this.subscriptionValidator = subscriptionValidator;
    }

    public List<Appointment> list(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNullOrderByStartsAtAsc(companyId);
    }

    public Appointment create(AppointmentRequest request) {
        subscriptionValidator.validateActiveSubscription(request.companyId(), "crear nuevas citas");
        validate(request);
        return repository.save(new Appointment(
                request.companyId(), request.leadId(), request.propertyId(), request.title().trim(),
                request.appointmentType(), request.status(), request.startsAt(), request.endsAt(),
                clean(request.location()), clean(request.notes())
        ));
    }

    @Transactional
    public Appointment update(UUID id, AppointmentRequest request) {
        validate(request);
        Appointment appointment = repository.findByIdAndCompanyIdAndDeletedAtIsNull(id, request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));
        appointment.update(request.leadId(), request.propertyId(), request.title().trim(),
                request.appointmentType(), request.status(), request.startsAt(), request.endsAt(),
                clean(request.location()), clean(request.notes()));
        return repository.save(appointment);
    }

    private void validate(AppointmentRequest request) {
        if (request.endsAt() != null && request.endsAt().isBefore(request.startsAt())) {
            throw new IllegalArgumentException("La hora final no puede ser anterior al inicio.");
        }
        if (request.leadId() != null) {
            leadRepository.findByIdAndCompanyIdAndDeletedAtIsNull(request.leadId(), request.companyId())
                    .orElseThrow(() -> new IllegalArgumentException("Prospecto no encontrado."));
        }
        if (request.propertyId() != null) {
            propertyRepository.findByIdAndCompanyIdAndDeletedAtIsNull(request.propertyId(), request.companyId())
                    .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada."));
        }
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
