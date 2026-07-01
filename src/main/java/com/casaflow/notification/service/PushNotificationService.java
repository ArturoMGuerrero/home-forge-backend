package com.casaflow.notification.service;

import com.casaflow.notification.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

    // TODO: Integrar con Firebase Cloud Messaging o OneSignal
    public void send(Notification notification) {
        logger.info("Sending push notification to: {}", notification.getRecipientId());
        logger.info("Content: {}", notification.getContent());

        // Simulación de envío exitoso
        // En producción, aquí iría la integración con FCM/OneSignal
        try {
            // fcmClient.send(userId, title, body);
            logger.info("Push notification sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send push notification", e);
            throw new RuntimeException("Failed to send push: " + e.getMessage());
        }
    }
}
