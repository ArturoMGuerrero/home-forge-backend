package com.homeforge.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación de la API en {@code /swagger-ui.html} (y el JSON en {@code /v3/api-docs}).
 * Para probar rutas privadas: inicia sesión con {@code POST /api/auth/login}, copia el {@code token}
 * de la respuesta y pégalo en el botón "Authorize". En producción está apagada (application-prod.yml).
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    OpenAPI homeForgeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HomeForge API")
                        .version("v1")
                        .description("""
                                API del CRM inmobiliario HomeForge.

                                - **Autenticación:** JWT (8 h). Obtén el token con `POST /api/auth/login` y úsalo en "Authorize".
                                - **Empresa:** cada usuario solo ve los datos de su inmobiliaria. Si una ruta recibe `companyId`, debe ser el de tu sesión (si no, 403).
                                - **Rutas públicas** (sin token): catálogo y detalle de propiedades, perfil público de la empresa, catálogos, formulario "Me interesa", vista previa para compartir y webhooks.
                                - **Errores:** cuerpo `{"error": "mensaje"}`. 400 validación, 401 sin sesión, 403 sin permiso, 402 suscripción vencida, 429 demasiados intentos.
                                """))
                .components(new Components().addSecuritySchemes(BEARER, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token de POST /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER));
    }
}
