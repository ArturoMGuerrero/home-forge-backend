package com.homeforge.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Rutas del sitio público. Muestran datos de todas las empresas, así que no se filtran por la
 * empresa del usuario aunque la petición traiga un token (p. ej. un asesor con sesión abierta).
 */
public final class PublicEndpoints {

    static final List<String> READ_PREFIXES = List.of(
            "/api/properties/public",
            "/api/companies/public/",
            "/api/catalogs",
            "/api/share/"
    );

    private PublicEndpoints() {}

    /** Si la petición HTTP en curso es de una página pública. */
    public static boolean isCurrentRequestPublic() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            String path = request.getRequestURI().substring(request.getContextPath().length());
            return "GET".equals(request.getMethod()) && READ_PREFIXES.stream().anyMatch(path::startsWith);
        }
        return false;
    }
}
