package com.homeforge.property;

import com.homeforge.property.controller.SharePreviewController;
import com.homeforge.property.domain.ListingType;
import com.homeforge.property.domain.PropertyStatus;
import com.homeforge.property.dto.PublicProperty;
import com.homeforge.property.dto.PublicPropertyResponse;
import com.homeforge.property.dto.SellerContact;
import com.homeforge.property.service.PublicPropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SharePreviewControllerTest {

    private final PublicPropertyService service = mock(PublicPropertyService.class);
    private final SharePreviewController controller = new SharePreviewController(service, "https://homeforge.mx/");

    private static PublicProperty property(UUID id, String title, String description) {
        return new PublicProperty(id, UUID.randomUUID(), "P-1", title, "HOUSE", ListingType.SALE, PropertyStatus.AVAILABLE,
                new BigDecimal("2500000"), "MXN", "MX", "QRO", "Querétaro", "Calle 1", null, null,
                3, new BigDecimal("2.5"), new BigDecimal("200"), new BigDecimal("180"), 2, description,
                null, List.of(new PublicProperty.Image(UUID.randomUUID(), "/uploads/properties/a/foto.jpg", 0)), Instant.now());
    }

    @Test
    void rendersOpenGraphTagsWithAbsoluteImageAndUrl() {
        UUID id = UUID.randomUUID();
        when(service.getPublished(id)).thenReturn(new PublicPropertyResponse(
                property(id, "Casa en Juriquilla", "Amplia casa con jardín."),
                new SellerContact(UUID.randomUUID(), "Inmobiliaria Sol", null, null)));

        ResponseEntity<String> response = controller.property(id);
        String html = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(html.contains("<meta property=\"og:title\" content=\"Casa en Juriquilla\">"));
        assertTrue(html.contains("<meta property=\"og:image\" content=\"https://homeforge.mx/uploads/properties/a/foto.jpg\">"));
        assertTrue(html.contains("<meta property=\"og:url\" content=\"https://homeforge.mx/propiedades/" + id + "\">"));
        assertTrue(html.contains("<meta property=\"og:site_name\" content=\"Inmobiliaria Sol\">"));
        assertTrue(html.contains("$2,500,000 MXN · Casa en venta · 3 recámaras · 2.5 baños · 180 m² · Querétaro. Amplia casa con jardín."));
    }

    @Test
    void escapesTextWrittenByTheAgency() {
        UUID id = UUID.randomUUID();
        when(service.getPublished(id)).thenReturn(new PublicPropertyResponse(
                property(id, "Casa \"bonita\"><script>alert(1)</script>", "<img src=x onerror=alert(1)>"),
                new SellerContact(UUID.randomUUID(), "Sol & Luna", null, null)));

        String html = controller.property(id).getBody();

        assertFalse(html.contains("<script>"));
        assertFalse(html.contains("<img"));
        assertTrue(html.contains("Casa &quot;bonita&quot;&gt;&lt;script&gt;"));
        assertTrue(html.contains("Sol &amp; Luna"));
    }

    @Test
    void unpublishedPropertyIsNotFound() {
        UUID id = UUID.randomUUID();
        when(service.getPublished(id)).thenThrow(new IllegalArgumentException("Propiedad pública no encontrada."));

        ResponseEntity<String> response = controller.property(id);

        assertEquals(404, response.getStatusCode().value());
        assertFalse(response.getBody().contains("og:image"));
    }
}
