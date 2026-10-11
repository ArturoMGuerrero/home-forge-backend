package com.homeforge.shared;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** La documentación OpenAPI se genera sin sesión e incluye las rutas, los grupos y el esquema JWT. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class ApiDocsIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    @Autowired
    private Environment environment;

    @Test
    void publishesTheOpenApiDocument() throws Exception {
        HttpResponse<String> docs = get("/v3/api-docs");
        assertEquals(200, docs.statusCode(), docs.body());
        String json = docs.body();
        assertTrue(json.contains("\"title\":\"HomeForge API\""));
        assertTrue(json.contains("\"/api/leads\""));
        assertTrue(json.contains("\"/api/auth/login\""));
        assertTrue(json.contains("\"name\":\"Prospectos\""), "los endpoints se agrupan en español");
        assertTrue(json.contains("\"bearerAuth\""), "botón Authorize con JWT");
    }

    @Test
    void servesSwaggerUi() throws Exception {
        HttpResponse<String> ui = get("/swagger-ui.html");
        assertEquals(200, ui.statusCode());
        assertTrue(ui.body().toLowerCase().contains("swagger"));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(URI.create("http://localhost:" + environment.getProperty("local.server.port") + path)).build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
