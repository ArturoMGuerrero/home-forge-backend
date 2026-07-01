package com.casaflow.notification.service;

import com.casaflow.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // TODO: Integrar con SendGrid o SMTP
    public void send(Notification notification) {
        logger.info("Sending email to: {}", notification.getRecipientEmail());
        logger.info("Subject: {}", notification.getSubject());
        logger.info("Content: {}", notification.getContent());

        // Simulación de envío exitoso
        // En producción, aquí iría la integración con SendGrid/SMTP
        try {
            // sendGridClient.send(email);
            logger.info("Email sent successfully to: {}", notification.getRecipientEmail());
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
