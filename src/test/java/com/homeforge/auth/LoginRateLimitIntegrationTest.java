package com.homeforge.auth;

import com.homeforge.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** El bloqueo por contraseñas incorrectas funciona de punta a punta (con el límite activado). */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "app.auth.rate-limit.enabled=true")
@Import(TestcontainersConfiguration.class)
class LoginRateLimitIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    @Test
    void locksTheAccountAfterFiveWrongPasswords() throws Exception {
        String email = "bloqueo-" + UUID.randomUUID() + "@example.com";
        HttpResponse<String> registered = post("/auth/register", """
                {"fullName":"Administrador","companyName":"Empresa Bloqueo","email":"%s","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(email));
        assertEquals(201, registered.statusCode(), registered.body());

        String wrong = "{\"email\":\"%s\",\"password\":\"Incorrecta1\"}".formatted(email);
        for (int i = 0; i < 5; i++) {
            assertEquals(401, post("/auth/login", wrong).statusCode());
        }

        HttpResponse<String> locked = post("/auth/login", "{\"email\":\"%s\",\"password\":\"Segura123\"}".formatted(email));
        assertEquals(429, locked.statusCode(), "aun con la contraseña correcta, la cuenta queda bloqueada");
        assertTrue(locked.headers().firstValue("Retry-After").isPresent());
        assertTrue(locked.body().contains("Demasiados intentos"));
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(URI.create("http://localhost:" + environment.getProperty("local.server.port") + "/api" + path))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
