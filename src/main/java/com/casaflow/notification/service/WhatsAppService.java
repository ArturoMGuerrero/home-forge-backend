package com.casaflow.notification.service;

import com.casaflow.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    // TODO: Integrar con WhatsApp Business API o Twilio
    public void send(Notification notification) {
        logger.info("Sending WhatsApp to: {}", notification.getRecipientPhone());
        logger.info("Content: {}", notification.getContent());

        // Simulación de envío exitoso
        // En producción, aquí iría la integración con WhatsApp Business API
        try {
            // whatsappClient.sendMessage(phone, content);
            logger.info("WhatsApp sent successfully to: {}", notification.getRecipientPhone());
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp", e);
            throw new RuntimeException("Failed to send WhatsApp: " + e.getMessage());
        }
    }
}
