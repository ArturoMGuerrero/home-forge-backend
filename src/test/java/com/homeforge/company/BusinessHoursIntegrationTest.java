package com.homeforge.company;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Horario de atención: valores por defecto, cambio por el administrador y validaciones. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class BusinessHoursIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    @Test
    void adminConfiguresTheBusinessHours() throws Exception {
        String registered = register("horario-" + UUID.randomUUID() + "@example.com");
        String token = field(registered, "token");
        String path = "/companies/" + field(registered, "companyId") + "/business-hours";

        HttpResponse<String> defaults = send("GET", path, null, token);
        assertEquals(200, defaults.statusCode(), defaults.body());
        assertEquals("09:00", field(defaults.body(), "start"));
        assertEquals("19:00", field(defaults.body(), "end"));
        assertTrue(defaults.body().contains("\"slotMinutes\":30"));

        HttpResponse<String> updated = send("PUT", path, "{\"start\":\"08:30\",\"end\":\"18:00\",\"slotMinutes\":60}", token);
        assertEquals(200, updated.statusCode(), updated.body());
        HttpResponse<String> after = send("GET", path, null, token);
        assertEquals("08:30", field(after.body(), "start"));
        assertEquals("18:00", field(after.body(), "end"));
        assertTrue(after.body().contains("\"slotMinutes\":60"));
    }

    @Test
    void rejectsInvalidHours() throws Exception {
        String registered = register("horario-mal-" + UUID.randomUUID() + "@example.com");
        String token = field(registered, "token");
        String path = "/companies/" + field(registered, "companyId") + "/business-hours";

        assertEquals(400, send("PUT", path, "{\"start\":\"18:00\",\"end\":\"09:00\",\"slotMinutes\":30}", token).statusCode(),
                "cierra antes de abrir");
        assertEquals(400, send("PUT", path, "{\"start\":\"09:00\",\"end\":\"18:00\",\"slotMinutes\":45}", token).statusCode(),
                "intervalo no permitido");
        assertEquals(400, send("PUT", path, "{\"start\":\"9am\",\"end\":\"18:00\",\"slotMinutes\":30}", token).statusCode(),
                "formato de hora");
    }

    @Test
    void agentsCanReadButNotChangeTheBusinessHours() throws Exception {
        String registered = register("horario-asesor-" + UUID.randomUUID() + "@example.com");
        String adminToken = field(registered, "token");
        String companyId = field(registered, "companyId");
        String agentEmail = "asesor-" + UUID.randomUUID() + "@example.com";
        HttpResponse<String> agent = send("POST", "/users", """
                {"companyId":"%s","requesterUserId":"%s","fullName":"Asesor","email":"%s","role":"AGENT","password":"Asesor123"}
                """.formatted(companyId, field(registered, "userId"), agentEmail), adminToken);
        assertTrue(agent.statusCode() == 200 || agent.statusCode() == 201, agent.body());
        String agentToken = field(send("POST", "/auth/login",
                "{\"email\":\"%s\",\"password\":\"Asesor123\"}".formatted(agentEmail), null).body(), "token");

        String path = "/companies/" + companyId + "/business-hours";
        assertEquals(200, send("GET", path, null, agentToken).statusCode());
        assertEquals(403, send("PUT", path, "{\"start\":\"08:00\",\"end\":\"18:00\",\"slotMinutes\":30}", agentToken).statusCode());
    }

    private String register(String email) throws Exception {
        String body = """
                {"fullName":"Administrador","companyName":"Empresa %s","email":"%s","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(UUID.randomUUID(), email);
        HttpResponse<String> response = send("POST", "/auth/register", body, null);
        assertEquals(201, response.statusCode(), response.body());
        return response.body();
    }

    private HttpResponse<String> send(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + environment.getProperty("local.server.port") + "/api" + path))
                .method(method, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json");
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String field(String json, String name) {
        Matcher matcher = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        assertTrue(matcher.find(), "falta " + name + " en " + json);
        return matcher.group(1);
    }
}
