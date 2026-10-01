package com.homeforge.security;

import com.homeforge.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifica autenticación y aislamiento entre empresas con peticiones HTTP reales. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class TenantIsolationIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    private String baseUrl;
    private Account alpha;
    private Account beta;

    record Account(String token, String companyId, String userId) {}

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + environment.getProperty("local.server.port") + "/api";
        alpha = register("Inmobiliaria Alfa");
        beta = register("Inmobiliaria Beta");
    }

    @Test
    void privateEndpointsRequireToken() throws Exception {
        HttpResponse<String> response = send("GET", "/leads?companyId=" + alpha.companyId(), null, null);
        assertEquals(401, response.statusCode());

        HttpResponse<String> forged = send("GET", "/leads?companyId=" + alpha.companyId(), null, "token-inventado");
        assertEquals(401, forged.statusCode());
    }

    @Test
    void publicEndpointsStayOpen() throws Exception {
        assertEquals(200, send("GET", "/properties/public", null, null).statusCode());
    }

    @Test
    void ownCompanyDataIsAccessible() throws Exception {
        assertEquals(200, send("GET", "/leads?companyId=" + alpha.companyId(), null, alpha.token()).statusCode());
    }

    @Test
    void cannotQueryAnotherCompanyById() throws Exception {
        HttpResponse<String> response = send("GET", "/leads?companyId=" + beta.companyId(), null, alpha.token());
        assertEquals(403, response.statusCode());
    }

    @Test
    void cannotCreateDataInAnotherCompany() throws Exception {
        String lead = """
                {"companyId":"%s","firstName":"Ana","lastName":"López","listingType":"SALE","currencyCode":"MXN"}
                """.formatted(beta.companyId());
        assertEquals(403, send("POST", "/leads", lead, alpha.token()).statusCode());
    }

    @Test
    void recordsOfAnotherCompanyAreInvisibleEvenByIdOnly() throws Exception {
        String availability = """
                {"companyId":"%s","userId":"%s","dayOfWeek":1,"startTime":"09:00:00","endTime":"17:00:00"}
                """.formatted(beta.companyId(), beta.userId());
        HttpResponse<String> created = send("POST", "/agent-availability", availability, beta.token());
        assertEquals(200, created.statusCode(), created.body());
        String availabilityId = field(created.body(), "id");

        assertEquals(200, send("GET", "/agent-availability/" + availabilityId, null, beta.token()).statusCode());

        HttpResponse<String> foreign = send("GET", "/agent-availability/" + availabilityId, null, alpha.token());
        assertNotEquals(200, foreign.statusCode());
        assertFalse(foreign.body().contains(beta.companyId()));

        HttpResponse<String> foreignDelete = send("DELETE", "/agent-availability/" + availabilityId, null, alpha.token());
        assertTrue(foreignDelete.statusCode() >= 400, "no debe poder borrar datos de otra empresa");
        assertEquals(200, send("GET", "/agent-availability/" + availabilityId, null, beta.token()).statusCode());
    }

    @Test
    void publicCatalogShowsEveryCompanyEvenWithASessionAndHidesOwnerData() throws Exception {
        String property = """
                {"companyId":"%s","code":"HF-%d","title":"Casa pública de Beta","propertyType":"HOUSE",
                 "listingType":"SALE","status":"AVAILABLE","price":2500000,"currencyCode":"MXN","countryCode":"MX",
                 "stateCode":"Chihuahua","city":"Chihuahua","published":true,
                 "ownerName":"Dueño Privado","ownerEmail":"dueno.privado@example.com","ownerPhone":"+526141234567"}
                """.formatted(beta.companyId(), System.nanoTime() % 100000);
        HttpResponse<String> created = send("POST", "/properties", property, beta.token());
        assertTrue(created.statusCode() < 300, created.body());

        // Un asesor de otra empresa con sesión abierta navega el sitio público.
        HttpResponse<String> catalog = send("GET", "/properties/public", null, alpha.token());
        assertEquals(200, catalog.statusCode());
        assertTrue(catalog.body().contains("Casa pública de Beta"), "el catálogo público debe incluir otras empresas");
        assertFalse(catalog.body().contains("Dueño Privado"));
        assertFalse(catalog.body().contains("dueno.privado@example.com"));
        assertFalse(catalog.body().contains("ownerPhone"));
    }

    private Account register(String companyName) throws Exception {
        String email = "admin-" + UUID.randomUUID() + "@example.com";
        String body = """
                {"fullName":"Administrador","companyName":"%s","email":"%s","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(companyName, email);
        HttpResponse<String> response = send("POST", "/auth/register", body, null);
        assertEquals(201, response.statusCode(), response.body());
        return new Account(field(response.body(), "token"), field(response.body(), "companyId"), field(response.body(), "userId"));
    }

    private HttpResponse<String> send(String method, String path, String json, String token) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + path))
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
