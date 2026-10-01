package com.homeforge.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Rechaza con 403 cualquier petición que intente operar sobre otra empresa: el {@code companyId}
 * de la URL, de los parámetros o del cuerpo debe coincidir con la empresa del token.
 * Las consultas además se filtran en Hibernate ({@link TenantIdentifierResolver}).
 */
@ControllerAdvice
public class TenantGuard extends RequestBodyAdviceAdapter implements HandlerInterceptor {

    private static final String COMPANY_ID = "companyId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CurrentUser.get().ifPresent(user -> {
            String[] params = request.getParameterValues(COMPANY_ID);
            if (params != null) {
                for (String value : params) {
                    requireSame(user.companyId(), value);
                }
            }
            if (request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) instanceof Map<?, ?> vars) {
                Object companyId = vars.get(COMPANY_ID);
                if (companyId != null) {
                    requireSame(user.companyId(), companyId.toString());
                }
                // La configuración personal solo la consulta o cambia su dueño (o un administrador).
                Object userId = vars.get("userId");
                if (userId != null && request.getRequestURI().endsWith("/settings") && !user.isAdmin()) {
                    requireSame(user.userId(), userId.toString());
                }
            }
        });
        return true;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        CurrentUser.get().ifPresent(user -> {
            Object companyId = companyIdOf(body);
            if (companyId != null) {
                requireSame(user.companyId(), companyId.toString());
            }
        });
        return body;
    }

    private static Object companyIdOf(Object body) {
        if (body instanceof Map<?, ?> map) {
            return map.get(COMPANY_ID);
        }
        for (String accessor : new String[]{COMPANY_ID, "getCompanyId"}) {
            try {
                Method method = body.getClass().getMethod(accessor);
                return method.invoke(body);
            } catch (NoSuchMethodException ignored) {
                // Este cuerpo no trae empresa; se prueba el siguiente accesor.
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return null;
    }

    private static void requireSame(UUID expected, String value) {
        UUID parsed;
        try {
            parsed = UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Identificador no válido");
        }
        if (!Objects.equals(expected, parsed)) {
            throw new AccessDeniedException("No tienes acceso a esta empresa");
        }
    }
}
