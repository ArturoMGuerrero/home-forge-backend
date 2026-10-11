package com.homeforge.lead;

import com.homeforge.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Formulario "Me interesa" del sitio público: crea el prospecto en la empresa dueña de la propiedad. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class PublicInquiryIntegrationTest {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Autowired
    private Environment environment;

    record Account(String token, String companyId) {}

    private Account owner;
    private String propertyId;

    @BeforeEach
    void publishProperty() throws Exception {
        owner = register("Inmobiliaria Dueña");
        String property = """
                {"companyId":"%s","code":"WEB-%d","title":"Casa con jardín","propertyType":"HOUSE",
                 "listingType":"SALE","status":"AVAILABLE","price":3200000,"currencyCode":"MXN","countryCode":"MX",
                 "stateCode":"Querétaro","city":"Querétaro","published":true}
                """.formatted(owner.companyId(), System.nanoTime() % 100000);
        HttpResponse<String> created = send("POST", "/properties", property, owner.token());
        assertTrue(created.statusCode() < 300, created.body());
        propertyId = field(created.body(), "id");
    }

    @Test
    void anonymousInquiryCreatesAWebsiteLeadWithTaskAndHistory() throws Exception {
        String phone = randomPhone();
        HttpResponse<String> sent = inquiry(phone, "Ana María López", "sábado por la mañana", true, "", null);
        assertEquals(202, sent.statusCode(), sent.body());

        String leads = send("GET", "/leads?companyId=" + owner.companyId(), null, owner.token()).body();
        assertTrue(leads.contains(phone), "el prospecto debe estar en la empresa dueña de la propiedad");
        assertTrue(leads.contains("\"source\":\"WEBSITE\""));
        String leadId = leadIdFor(leads, phone);

        String history = send("GET", "/leads/" + leadId + "/activities?companyId=" + owner.companyId(), null, owner.token()).body();
        assertTrue(history.contains("Casa con jardín"));
        assertTrue(history.contains("Horario preferido para visitar: sábado por la mañana"));
        assertTrue(history.contains(propertyId), "la solicitud queda ligada a la propiedad");

        String tasks = send("GET", "/follow-up-tasks/lead/" + leadId + "?companyId=" + owner.companyId(), null, owner.token()).body();
        assertTrue(tasks.contains("Primer contacto"), tasks);
    }

    @Test
    void repeatedInquiryFromAnotherCompanysSessionDoesNotDuplicateTheLead() throws Exception {
        String phone = randomPhone();
        assertEquals(202, inquiry(phone, "Luis Pérez", null, true, "", null).statusCode());

        // Un asesor de otra empresa, con su sesión abierta, también deja sus datos.
        Account other = register("Otra Inmobiliaria");
        assertEquals(202, inquiry(phone, "Luis Pérez", null, true, "", other.token()).statusCode());

        String leads = send("GET", "/leads?companyId=" + owner.companyId(), null, owner.token()).body();
        assertEquals(1, leads.split(Pattern.quote(phone), -1).length - 1, "no debe duplicar el prospecto");
        assertFalse(send("GET", "/leads?companyId=" + other.companyId(), null, other.token()).body().contains(phone),
                "el prospecto no debe quedar en la empresa de quien tenía la sesión abierta");

        String tasks = send("GET", "/follow-up-tasks/lead/" + leadIdFor(leads, phone) + "?companyId=" + owner.companyId(), null, owner.token()).body();
        assertTrue(tasks.contains("Responder solicitud del sitio web"), tasks);
    }

    @Test
    void spamAndInvalidRequestsDoNotCreateLeads() throws Exception {
        String phone = randomPhone();
        assertEquals(202, inquiry(phone, "Robot", null, true, "https://spam.example.com", null).statusCode(),
                "al robot se le responde como a una persona");
        assertFalse(send("GET", "/leads?companyId=" + owner.companyId(), null, owner.token()).body().contains(phone));

        assertEquals(400, inquiry(phone, "Sin aviso", null, false, "", null).statusCode(), "debe aceptar el aviso de privacidad");
        assertEquals(400, inquiry("6141234567", "Sin lada", null, true, "", null).statusCode(), "teléfono sin formato internacional");

        HttpResponse<String> unknown = send("POST", "/properties/public/" + UUID.randomUUID() + "/inquiries",
                body(phone, "Alguien", null, true, ""), null);
        assertEquals(404, unknown.statusCode());
    }

    private HttpResponse<String> inquiry(String phone, String name, String preferredVisit, boolean privacy, String website, String token) throws Exception {
        return send("POST", "/properties/public/" + propertyId + "/inquiries", body(phone, name, preferredVisit, privacy, website), token);
    }

    private static String body(String phone, String name, String preferredVisit, boolean privacy, String website) {
        return """
                {"fullName":"%s","phoneE164":"%s","email":"","message":"Me interesa, ¿sigue disponible?",
                 "preferredVisit":%s,"acceptsPrivacy":%s,"website":"%s"}
                """.formatted(name, phone, preferredVisit == null ? "null" : "\"" + preferredVisit + "\"", privacy, website);
    }

    private static String randomPhone() {
        return "+5244" + String.format("%08d", (long) (Math.random() * 100_000_000));
    }

    /** id del prospecto cuyo objeto JSON contiene ese teléfono. */
    private static String leadIdFor(String leadsJson, String phone) {
        for (String lead : leadsJson.split("\\},\\{")) {
            if (lead.contains(phone)) {
                return field(lead, "id");
            }
        }
        throw new AssertionError("no se encontró el prospecto con " + phone);
    }

    private Account register(String companyName) throws Exception {
        String body = """
                {"fullName":"Administrador","companyName":"%s %s","email":"admin-%s@example.com","phoneE164":"+524421234567","password":"Segura123"}
                """.formatted(companyName, UUID.randomUUID(), UUID.randomUUID());
        HttpResponse<String> response = send("POST", "/auth/register", body, null);
        assertEquals(201, response.statusCode(), response.body());
        return new Account(field(response.body(), "token"), field(response.body(), "companyId"));
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
