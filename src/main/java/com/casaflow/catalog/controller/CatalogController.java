package com.casaflow.catalog.controller;

import com.casaflow.catalog.dto.CatalogItem;
import com.casaflow.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, List<String>> list() {
        return Map.of("catalogs", service.names());
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<CatalogItem>> get(@PathVariable String name) {
        return service.find(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
