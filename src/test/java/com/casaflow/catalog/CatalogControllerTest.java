package com.casaflow.catalog;

import com.casaflow.catalog.controller.CatalogController;
import com.casaflow.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CatalogController(new CatalogService())).build();
    }

    @Test
    void returnsCatalogItems() throws Exception {
        mockMvc.perform(get("/api/catalogs/property-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("HOUSE"))
                .andExpect(jsonPath("$[0].labelEs").value("Casa"))
                .andExpect(jsonPath("$[0].labelEn").value("House"));
    }

    @Test
    void returnsNotFoundForUnknownCatalog() throws Exception {
        mockMvc.perform(get("/api/catalogs/unknown"))
                .andExpect(status().isNotFound());
    }
}
