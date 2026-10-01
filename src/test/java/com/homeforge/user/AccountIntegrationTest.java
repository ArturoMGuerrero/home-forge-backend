package com.homeforge.user;

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

/** "Mi cuenta" por HTTP: perfil propio y cambio de contraseña que cierra las otras sesiones. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class AccountIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    @Test
    void updatesOwnProfile() throws Exception {
        String token = field(register("perfil-" + UUID.randomUUID() + "@example.com").body(), "token");

        HttpResponse<String> updated = send("PATCH", "/account/profile",
                "{\"fullName\":\"María Pérez\",\"phoneE164\":\"+526141234567\"}", token);
        assertEquals(200, updated.statusCode(), updated.body());

        HttpResponse<String> me = send("GET", "/account", null, token);
        assertEquals("María Pérez", field(me.body(), "fullName"));
        assertEquals("+526141234567", field(me.body(), "phoneE164"));

        assertEquals(400, send("PATCH", "/account/profile", "{\"fullName\":\"X\",\"phoneE164\":\"6141234567\"}", token).statusCode());
    }

    @Test
    void changingPasswordRevokesOtherSessions() throws Exception {
        String email = "clave-" + UUID.randomUUID() + "@example.com";
        String oldToken = field(register(email).body(), "token");
        Thread.sleep(1100); // el nuevo token debe emitirse en un segundo posterior al anterior

        HttpResponse<String> wrong = send("POST", "/account/password",
                "{\"currentPassword\":\"Incorrecta1\",\"newPassword\":\"NuevaClave123\"}", oldToken);
        assertEquals(400, wrong.statusCode());

        HttpResponse<String> changed = send("POST", "/account/password",
                "{\"currentPassword\":\"Segura123\",\"newPassword\":\"NuevaClave123\"}", oldToken);
        assertEquals(200, changed.statusCode(), changed.body());
        String newToken = field(changed.body(), "token");

        assertEquals(401, send("GET", "/account", null, oldToken).statusCode(), "el token anterior debe quedar inválido");
        assertEquals(200, send("GET", "/account", null, newToken).statusCode());

        String login = "{\"email\":\"%s\",\"password\":\"%s\"}";
        assertEquals(401, send("POST", "/auth/login", login.formatted(email, "Segura123"), null).statusCode());
        assertEquals(200, send("POST", "/auth/login", login.formatted(email, "NuevaClave123"), null).statusCode());
    }

    private HttpResponse<String> register(String email) throws Exception {
        String body = """
                {"fullName":"Administrador","companyName":"Empresa %s","email":"%s","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(UUID.randomUUID(), email);
        HttpResponse<String> response = send("POST", "/auth/register", body, null);
        assertEquals(201, response.statusCode(), response.body());
        return response;
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
        Matcher matcher = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        assertTrue(matcher.find(), "falta el campo " + name + " en " + json);
        return matcher.group(1);
    }
}
