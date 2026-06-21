package com.casaflow.catalog;

import com.casaflow.catalog.service.CatalogService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogServiceTest {

    private final CatalogService service = new CatalogService();

    @Test
    void exposesCoreCatalogs() {
        assertThat(service.names()).contains(
                "lead-statuses",
                "lead-sources",
                "property-statuses",
                "property-types",
                "document-types",
                "mortgage-types",
                "countries",
                "currencies"
        );
    }

    @Test
    void returnsTranslatedCatalogItems() {
        var statuses = service.find("lead-statuses").orElseThrow();

        assertThat(statuses)
                .extracting(item -> item.code())
                .containsExactly("NEW", "CONTACTED", "QUALIFIED", "TOUR_SCHEDULED",
                        "TOUR_COMPLETED", "OFFER_MADE", "UNDER_CONTRACT", "CLOSED", "LOST");
        assertThat(statuses.getFirst().labelEs()).isEqualTo("Nuevo");
        assertThat(statuses.getFirst().labelEn()).isEqualTo("New");
    }

    @Test
    void returnsEmptyForUnknownCatalog() {
        assertThat(service.find("unknown")).isEmpty();
    }
}
