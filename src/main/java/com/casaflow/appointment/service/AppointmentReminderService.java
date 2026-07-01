package com.casaflow.appointment.service;

import com.casaflow.appointment.domain.Appointment;
import com.casaflow.appointment.domain.AppointmentStatus;
import com.casaflow.appointment.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AppointmentReminderService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderService.class);

    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    public AppointmentReminderService(
            AppointmentRepository appointmentRepository,
            AppointmentService appointmentService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void processReminders() {
        logger.info("Processing appointment reminders...");

        Instant now = Instant.now();
        Instant futureWindow = now.plus(2, ChronoUnit.HOURS);

        List<Appointment> allAppointments = appointmentRepository.findAll();

        for (Appointment appointment : allAppointments) {
            if (shouldSendReminder(appointment, now)) {
                sendReminder(appointment);
            }
        }
    }

    private boolean shouldSendReminder(Appointment appointment, Instant now) {
        if (appointment.getDeletedAt() != null) return false;
        if (appointment.getReminderSent()) return false;
        if (appointment.getReminderMinutes() == null || appointment.getReminderMinutes() == 0) return false;
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) return false;
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) return false;

        Instant reminderTime = appointment.getStartTime().minus(appointment.getReminderMinutes(), ChronoUnit.MINUTES);
        return now.isAfter(reminderTime) || now.equals(reminderTime);
    }

    private void sendReminder(Appointment appointment) {
        try {
            logger.info("Sending reminder for appointment: {} ({})", appointment.getId(), appointment.getTitle());

            // TODO: Integrate with NotificationService to send actual notifications
            // For now, just mark as sent

            appointmentService.markReminderSent(appointment.getId());
            logger.info("Reminder sent successfully for appointment: {}", appointment.getId());
        } catch (Exception e) {
            logger.error("Failed to send reminder for appointment: {}", appointment.getId(), e);
        }
    }
}
