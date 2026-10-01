package com.homeforge.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private static final String UNAUTHORIZED = "Inicia sesión para continuar";
    private static final String FORBIDDEN = "No tienes permiso para esta acción";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, CurrentUserConverter currentUserConverter) throws Exception {
        http
                // API sin cookies: el token viaja en el encabezado Authorization.
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Chequeo de salud para el balanceador / la plataforma de despliegue (sin detalles)
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        // Acceso, registro y recuperación de contraseña
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register",
                                "/api/auth/request-password-reset", "/api/auth/reset-password").permitAll()
                        // Sitio público
                        .requestMatchers(HttpMethod.GET, "/api/properties/public", "/api/properties/public/**",
                                "/api/companies/public/**", "/api/catalogs", "/api/catalogs/**", "/uploads/**").permitAll()
                        // Avisos de los proveedores de pago (servidor a servidor; cada uno valida su firma)
                        .requestMatchers(HttpMethod.POST, "/api/webhooks/mercadopago", "/api/webhooks/stripe").permitAll()
                        // Administración de la empresa: solo administradores
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*", "/api/users/*/status", "/api/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payments/subscriptions", "/api/billing/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/teams", "/api/teams/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/teams/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assignment-rules").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/assignment-rules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/assignment-rules/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(currentUserConverter))
                        .authenticationEntryPoint((request, response, ex) ->
                                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED))
                        .accessDeniedHandler((request, response, ex) ->
                                writeError(response, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED))
                        .accessDeniedHandler((request, response, e) ->
                                writeError(response, HttpServletResponse.SC_FORBIDDEN, FORBIDDEN)));
        return http.build();
    }

    @Bean
    JwtEncoder jwtEncoder(JwtSecretProvider secretProvider) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretProvider.key()));
    }

    @Bean
    JwtDecoder jwtDecoder(JwtSecretProvider secretProvider) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretProvider.key())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(TokenService.ISSUER));
        return decoder;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Content-Disposition"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/uploads/**", config);
        return source;
    }

    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
