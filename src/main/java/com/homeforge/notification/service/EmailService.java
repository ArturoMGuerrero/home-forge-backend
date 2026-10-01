package com.homeforge.notification.service;

import com.homeforge.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider, @Value("${spring.mail.from:noreply@homeforge.app}") String fromAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
    }

    public void sendPasswordReset(String recipientEmail, String resetUrl) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new MailSendException("SMTP is not configured");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipientEmail);
        message.setSubject("Restablece tu contraseña de HomeForge");
        message.setText("Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Usa este enlace durante los próximos 30 minutos:\n" + resetUrl
                + "\n\nSi no solicitaste este cambio, ignora este correo.");
        mailSender.send(message);
    }

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
