package com.homeforge.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clave HMAC para firmar los tokens. En producción viene de JWT_SECRET (32+ caracteres).
 * En desarrollo, si no está definida, se genera una vez y se guarda en .jwt-dev-secret (ignorado por git).
 */
@Component
public class JwtSecretProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtSecretProvider.class);
    private static final int MIN_LENGTH = 32;

    private final SecretKey key;

    public JwtSecretProvider(
            @Value("${app.jwt.secret:}") String configuredSecret,
            @Value("${app.jwt.dev-secret-file:.jwt-dev-secret}") String devSecretFile,
            @Value("${app.jwt.require-secret:false}") boolean requireSecret
    ) {
        if (requireSecret && configuredSecret.isBlank()) {
            throw new IllegalStateException("Falta JWT_SECRET: en producción es obligatoria (32+ caracteres)");
        }
        String secret = configuredSecret.isBlank() ? devSecret(Path.of(devSecretFile)) : configuredSecret;
        if (secret.length() < MIN_LENGTH) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos " + MIN_LENGTH + " caracteres");
        }
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public SecretKey key() {
        return key;
    }

    private static String devSecret(Path file) {
        try {
            if (Files.exists(file)) {
                return Files.readString(file).trim();
            }
            byte[] bytes = new byte[48];
            new SecureRandom().nextBytes(bytes);
            String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            Files.writeString(file, secret);
            log.warn("JWT_SECRET no está definida: se generó una clave de desarrollo en {}. En producción define JWT_SECRET.",
                    file.toAbsolutePath());
            return secret;
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo leer o crear la clave JWT de desarrollo", ex);
        }
    }
}
