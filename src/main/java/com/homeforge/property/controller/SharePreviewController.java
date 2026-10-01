package com.homeforge.property.controller;

import com.homeforge.property.domain.ListingType;
import com.homeforge.property.dto.PublicProperty;
import com.homeforge.property.dto.PublicPropertyResponse;
import com.homeforge.property.service.PublicPropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

/**
 * Vista previa de una propiedad para WhatsApp, Facebook, Telegram y similares. Sus robots no ejecutan
 * JavaScript, así que no ven el contenido de la app: nginx les manda aquí cuando piden
 * {@code /propiedades/{id}} y reciben un HTML con las etiquetas Open Graph (título, precio y foto).
 */
@RestController
@RequestMapping("/api/share")
public class SharePreviewController {

    private static final MediaType HTML = new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8);
    private static final Locale MX = Locale.forLanguageTag("es-MX");
    private static final int MAX_DESCRIPTION = 200;
    private static final Map<String, String> PROPERTY_TYPES = Map.of(
            "HOUSE", "Casa",
            "APARTMENT", "Departamento",
            "LAND", "Terreno",
            "COMMERCIAL", "Local comercial",
            "OFFICE", "Oficina",
            "WAREHOUSE", "Bodega"
    );

    private final PublicPropertyService publicPropertyService;
    private final String frontendUrl;

    public SharePreviewController(
            PublicPropertyService publicPropertyService,
            @Value("${app.frontend-url:http://localhost:5174}") String frontendUrl
    ) {
        this.publicPropertyService = publicPropertyService;
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @GetMapping("/propiedades/{propertyId}")
    public ResponseEntity<String> property(@PathVariable UUID propertyId) {
        PublicPropertyResponse response;
        try {
            response = publicPropertyService.getPublished(propertyId);
        } catch (IllegalArgumentException notPublished) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(HTML)
                    .body(page("Propiedad no disponible · HomeForge", "Esta propiedad ya no está publicada.",
                            null, frontendUrl + "/propiedades", "HomeForge"));
        }
        PublicProperty property = response.property();
        String siteName = response.seller() != null && response.seller().companyName() != null
                ? response.seller().companyName() : "HomeForge";
        return ResponseEntity.ok()
                .contentType(HTML)
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .body(page(property.title(), description(property), imageUrl(property),
                        frontendUrl + "/propiedades/" + property.id(), siteName));
    }

    /** "$2,500,000 MXN · Casa en venta · 3 recámaras · 2 baños · 180 m² · Querétaro. Descripción…" */
    static String description(PublicProperty property) {
        List<String> parts = new ArrayList<>();
        if (property.price() != null) {
            parts.add(price(property.price(), property.currencyCode()));
        }
        String type = PROPERTY_TYPES.getOrDefault(property.propertyType(), "Propiedad");
        parts.add(type + (property.listingType() == ListingType.RENT ? " en renta" : " en venta"));
        if (property.bedrooms() != null && property.bedrooms() > 0) {
            parts.add(property.bedrooms() + (property.bedrooms() == 1 ? " recámara" : " recámaras"));
        }
        if (property.bathrooms() != null && property.bathrooms().signum() > 0) {
            parts.add(property.bathrooms().stripTrailingZeros().toPlainString() + " baños");
        }
        BigDecimal area = property.constructionArea() != null ? property.constructionArea() : property.landArea();
        if (area != null && area.signum() > 0) {
            parts.add(area.stripTrailingZeros().toPlainString() + " m²");
        }
        if (property.city() != null && !property.city().isBlank()) {
            parts.add(property.city());
        }
        String summary = String.join(" · ", parts);
        String text = property.description() == null ? "" : property.description().replaceAll("\\s+", " ").trim();
        if (text.isEmpty()) {
            return summary;
        }
        String full = summary + ". " + text;
        return full.length() <= MAX_DESCRIPTION ? full : full.substring(0, MAX_DESCRIPTION - 1).trim() + "…";
    }

    private static String price(BigDecimal amount, String currencyCode) {
        String code = currencyCode == null || currencyCode.isBlank() ? "MXN" : currencyCode;
        NumberFormat format = NumberFormat.getCurrencyInstance(MX);
        try {
            format.setCurrency(Currency.getInstance(code));
        } catch (IllegalArgumentException unknownCurrency) {
            // Se deja el símbolo por defecto; el código va al final de todos modos.
        }
        format.setMaximumFractionDigits(0);
        return format.format(amount) + " " + code;
    }

    /** Las redes piden la foto con URL absoluta; las de /uploads se sirven en el mismo dominio que la app. */
    private String imageUrl(PublicProperty property) {
        String url = property.images() != null && !property.images().isEmpty()
                ? property.images().getFirst().imageUrl()
                : property.imageUrl();
        if (url == null || url.isBlank()) {
            return null;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return frontendUrl + (url.startsWith("/") ? url : "/" + url);
    }

    private static String page(String title, String description, String imageUrl, String url, String siteName) {
        String t = htmlEscape(title, "UTF-8");
        String d = htmlEscape(description, "UTF-8");
        String u = htmlEscape(url, "UTF-8");
        StringBuilder html = new StringBuilder()
                .append("<!doctype html>\n<html lang=\"es\">\n<head>\n<meta charset=\"utf-8\">\n")
                .append("<title>").append(t).append("</title>\n")
                .append("<meta name=\"description\" content=\"").append(d).append("\">\n")
                .append("<meta property=\"og:type\" content=\"website\">\n")
                .append("<meta property=\"og:locale\" content=\"es_MX\">\n")
                .append("<meta property=\"og:site_name\" content=\"").append(htmlEscape(siteName, "UTF-8")).append("\">\n")
                .append("<meta property=\"og:title\" content=\"").append(t).append("\">\n")
                .append("<meta property=\"og:description\" content=\"").append(d).append("\">\n")
                .append("<meta property=\"og:url\" content=\"").append(u).append("\">\n");
        if (imageUrl != null) {
            String i = htmlEscape(imageUrl, "UTF-8");
            html.append("<meta property=\"og:image\" content=\"").append(i).append("\">\n")
                    .append("<meta property=\"og:image:alt\" content=\"").append(t).append("\">\n")
                    .append("<meta name=\"twitter:card\" content=\"summary_large_image\">\n")
                    .append("<meta name=\"twitter:image\" content=\"").append(i).append("\">\n");
        } else {
            html.append("<meta name=\"twitter:card\" content=\"summary\">\n");
        }
        return html
                .append("<link rel=\"canonical\" href=\"").append(u).append("\">\n")
                .append("<meta http-equiv=\"refresh\" content=\"0; url=").append(u).append("\">\n")
                .append("</head>\n<body>\n<p><a href=\"").append(u).append("\">").append(t).append("</a></p>\n")
                .append("</body>\n</html>\n")
                .toString();
    }
}
