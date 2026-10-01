package com.homeforge.security;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Aislamiento por empresa: las entidades con {@code @TenantId} se filtran automáticamente por la
 * empresa del usuario autenticado. Sin usuario (sitio público, login, tareas programadas) la sesión
 * es "root" y no se filtra; esos flujos ya restringen sus consultas por sí mismos.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID>, HibernatePropertiesCustomizer {

    static final UUID ROOT = new UUID(0L, 0L);

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        if (PublicEndpoints.isCurrentRequestPublic()) {
            return ROOT;
        }
        return CurrentUser.get().map(CurrentUser::companyId).orElse(ROOT);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public boolean isRoot(UUID tenantId) {
        return ROOT.equals(tenantId);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
