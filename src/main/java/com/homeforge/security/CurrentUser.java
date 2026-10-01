package com.homeforge.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/** Usuario autenticado de la petición actual; la empresa siempre sale del token, nunca del cliente. */
public record CurrentUser(UUID userId, UUID companyId, String email, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /** Vacío en procesos sin petición de usuario (tareas programadas, endpoints públicos). */
    public static Optional<CurrentUser> get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
