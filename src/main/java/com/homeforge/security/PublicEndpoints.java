package com.homeforge.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Rutas del sitio público. Muestran datos de todas las empresas, así que no se filtran por la
 * empresa del usuario aunque la petición traiga un token (p. ej. un asesor con sesión abierta).
 */
public final class PublicEndpoints {

    static final List<String> READ_PREFIXES = List.of(
            "/api/properties/public",
            "/api/companies/public/",
            "/api/catalogs"
    );

    static final Pattern INQUIRY_PATH = Pattern.compile("^/api/properties/public/[0-9a-fA-F-]{36}/inquiries$");

    private PublicEndpoints() {}

    /** Si la petición HTTP en curso es de una página pública. */
    public static boolean isCurrentRequestPublic() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if ("POST".equals(request.getMethod())) {
                // Solicitudes del sitio público: el prospecto se guarda en la empresa dueña de la propiedad,
                // aunque quien la envía tenga la sesión abierta en otra empresa.
                return INQUIRY_PATH.matcher(path).matches();
            }
            return "GET".equals(request.getMethod()) && READ_PREFIXES.stream().anyMatch(path::startsWith);
        }
        return false;
    }
}
