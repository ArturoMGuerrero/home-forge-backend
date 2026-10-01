package com.homeforge.document;

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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Flujo de contratos por HTTP: plantilla → contrato con variables → cambio de estado. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class ContractsIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    record Account(String token, String companyId, String userId) {}

    @Test
    void generatesContractFromTemplateAndKeepsItPrivate() throws Exception {
        Account owner = register("Inmobiliaria Contratos");
        Account other = register("Otra Inmobiliaria");

        String template = """
                {"companyId":"%s","name":"Contrato de compraventa","documentType":"PURCHASE",
                 "content":"Comprador: {{cliente_nombre}}\\nInmueble: {{propiedad_titulo}}\\nPrecio: {{precio}}"}
                """.formatted(owner.companyId());
        HttpResponse<String> createdTemplate = send("POST", "/document-templates", template, owner.token());
        assertTrue(createdTemplate.statusCode() < 300, createdTemplate.body());
        String templateId = field(createdTemplate.body(), "id");

        String contract = """
                {"companyId":"%s","templateId":"%s","name":"Compraventa Casa Centro","documentType":"PURCHASE",
                 "createdByUserId":"%s",
                 "variables":{"cliente_nombre":"Ana López","propiedad_titulo":"Casa Centro","precio":"$2,500,000"}}
                """.formatted(owner.companyId(), templateId, UUID.randomUUID());
        HttpResponse<String> created = send("POST", "/contracts", contract, owner.token());
        assertEquals(201, created.statusCode(), created.body());
        String contractId = field(created.body(), "id");
        assertTrue(created.body().contains("Comprador: Ana López"), created.body());
        assertTrue(created.body().contains("Precio: $2,500,000"));
        // El autor lo decide la sesión, no el cuerpo de la petición.
        assertEquals(owner.userId(), field(created.body(), "createdByUserId"));

        HttpResponse<String> list = send("GET", "/contracts?companyId=" + owner.companyId(), null, owner.token());
        assertTrue(list.body().contains(contractId));

        HttpResponse<String> status = send("PATCH", "/contracts/" + contractId + "/status?companyId=" + owner.companyId(),
                "{\"status\":\"COMPLETED\"}", owner.token());
        assertEquals(200, status.statusCode(), status.body());
        assertEquals("COMPLETED", field(status.body(), "status"));

        // Otra empresa no lo ve aunque conozca el id.
        assertEquals(403, send("GET", "/contracts/" + contractId + "?companyId=" + owner.companyId(), null, other.token()).statusCode());
        assertNotEquals(200, send("GET", "/contracts/" + contractId + "?companyId=" + other.companyId(), null, other.token()).statusCode());
    }

    private Account register(String companyName) throws Exception {
        String body = """
                {"fullName":"Administrador","companyName":"%s","email":"admin-%s@example.com","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(companyName, UUID.randomUUID());
        HttpResponse<String> response = send("POST", "/auth/register", body, null);
        assertEquals(201, response.statusCode(), response.body());
        return new Account(field(response.body(), "token"), field(response.body(), "companyId"), field(response.body(), "userId"));
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
