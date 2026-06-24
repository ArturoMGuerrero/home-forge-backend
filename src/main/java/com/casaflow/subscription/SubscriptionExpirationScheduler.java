package com.casaflow.subscription;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Scheduler que verifica y actualiza el estado de suscripciones expiradas.
 * Se ejecuta diariamente a las 2 AM para marcar como EXPIRED las suscripciones vencidas.
 */
@Component
public class SubscriptionExpirationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionExpirationScheduler.class);
    private final CompanyRepository companyRepository;

    public SubscriptionExpirationScheduler(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Verifica y marca como EXPIRED las suscripciones activas que han vencido.
     * Se ejecuta diariamente a las 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void checkExpiredActiveSubscriptions() {
        logger.info("Iniciando verificación de suscripciones activas expiradas...");

        try {
            Instant now = Instant.now();
            List<Company> companies = companyRepository.findAll();
            int expiredCount = 0;

            for (Company company : companies) {
                if ("ACTIVE".equals(company.getSubscriptionStatus())
                    && company.getNextBillingAt() != null
                    && company.getNextBillingAt().isBefore(now)) {

                    logger.info("Marcando suscripción ACTIVA como EXPIRED para empresa: {} (ID: {})",
                        company.getName(), company.getId());

                    company.updateSubscriptionStatus("EXPIRED", company.getNextBillingAt());
                    companyRepository.save(company);
                    expiredCount++;
                }
            }

            logger.info("Verificación completada. {} suscripción(es) activa(s) marcada(s) como EXPIRED", expiredCount);

        } catch (Exception e) {
            logger.error("Error al verificar suscripciones activas expiradas", e);
        }
    }

    /**
     * Verifica y marca como EXPIRED los trials que han vencido.
     * Se ejecuta diariamente a las 3:00 AM.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void checkExpiredTrials() {
        logger.info("Iniciando verificación de trials expirados...");

        try {
            Instant now = Instant.now();
            List<Company> companies = companyRepository.findAll();
            int expiredCount = 0;

            for (Company company : companies) {
                if ("TRIAL".equals(company.getSubscriptionStatus())
                    && company.getTrialEndsAt() != null
                    && company.getTrialEndsAt().isBefore(now)) {

                    logger.info("Marcando TRIAL como EXPIRED para empresa: {} (ID: {})",
                        company.getName(), company.getId());

                    company.updateSubscriptionStatus("EXPIRED", company.getTrialEndsAt());
                    companyRepository.save(company);
                    expiredCount++;
                }
            }

            logger.info("Verificación completada. {} trial(s) marcado(s) como EXPIRED", expiredCount);

        } catch (Exception e) {
            logger.error("Error al verificar trials expirados", e);
        }
    }

    /**
     * Método manual para verificar y actualizar expirados inmediatamente.
     * Útil para testing o ejecución manual.
     */
    @Transactional
    public void checkAllExpiredSubscriptionsNow() {
        logger.info("Ejecución manual: verificando todas las suscripciones expiradas...");
        checkExpiredActiveSubscriptions();
        checkExpiredTrials();
        logger.info("Ejecución manual completada.");
    }
}
