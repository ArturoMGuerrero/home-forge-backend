package com.homeforge.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtSecretProviderTest {

    @TempDir
    Path dir;

    @Test
    void inProductionFailsWithoutJwtSecret() {
        String devFile = dir.resolve(".jwt-dev-secret").toString();
        assertThrows(IllegalStateException.class, () -> new JwtSecretProvider("", devFile, true));
        assertFalse(dir.resolve(".jwt-dev-secret").toFile().exists(), "no debe generar una clave de desarrollo");
    }

    @Test
    void inProductionUsesTheConfiguredSecret() {
        String devFile = dir.resolve(".jwt-dev-secret").toString();
        assertDoesNotThrow(() -> new JwtSecretProvider("x".repeat(40), devFile, true));
    }

    @Test
    void inDevelopmentGeneratesASecretWhenMissing() {
        String devFile = dir.resolve(".jwt-dev-secret").toString();
        assertDoesNotThrow(() -> new JwtSecretProvider("", devFile, false));
        assertTrue(dir.resolve(".jwt-dev-secret").toFile().exists());
    }
}
