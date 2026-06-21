package com.casaflow.property.controller;

import com.casaflow.property.domain.Property;
import com.casaflow.property.dto.CreatePropertyRequest;
import com.casaflow.property.dto.PublicPropertyResponse;
import com.casaflow.property.dto.ReorderImagesRequest;
import com.casaflow.property.service.PropertyService;
import com.casaflow.property.service.PublicPropertyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService service;
    private final PublicPropertyService publicPropertyService;

    public PropertyController(PropertyService service, PublicPropertyService publicPropertyService) {
        this.service = service;
        this.publicPropertyService = publicPropertyService;
    }

    @GetMapping
    public List<Property> list(@RequestParam UUID companyId) {
        return service.byCompany(companyId);
    }

    @GetMapping("/public")
    public List<PublicPropertyResponse> published() {
        return publicPropertyService.published();
    }

    @GetMapping("/public/{propertyId}")
    public PublicPropertyResponse publicProperty(@PathVariable UUID propertyId) {
        return publicPropertyService.getPublished(propertyId);
    }

    @GetMapping("/{propertyId}")
    public Property get(@PathVariable UUID propertyId, @RequestParam UUID companyId) {
        return service.get(propertyId, companyId);
    }

    @PostMapping
    public Property create(@Valid @RequestBody CreatePropertyRequest request) {
        return service.create(request);
    }

    @PutMapping("/{propertyId}")
    public Property update(@PathVariable UUID propertyId, @Valid @RequestBody CreatePropertyRequest request) {
        return service.update(propertyId, request);
    }

    @PostMapping("/{propertyId}/images")
    public Property addImages(
            @PathVariable UUID propertyId,
            @RequestParam UUID companyId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return service.addImages(propertyId, companyId, files);
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    public Property deleteImage(
            @PathVariable UUID propertyId,
            @PathVariable UUID imageId,
            @RequestParam UUID companyId
    ) {
        return service.deleteImage(propertyId, imageId, companyId);
    }

    @PutMapping("/{propertyId}/images/{imageId}/cover")
    public Property setCover(
            @PathVariable UUID propertyId,
            @PathVariable UUID imageId,
            @RequestParam UUID companyId
    ) {
        return service.setCover(propertyId, imageId, companyId);
    }

    @PutMapping("/{propertyId}/images/reorder")
    public Property reorderImages(
            @PathVariable UUID propertyId,
            @RequestParam UUID companyId,
            @Valid @RequestBody ReorderImagesRequest request
    ) {
        Map<UUID, Integer> newOrders = request.images().stream()
                .collect(Collectors.toMap(
                        ReorderImagesRequest.ImageOrder::id,
                        ReorderImagesRequest.ImageOrder::sortOrder
                ));
        return service.reorderImages(propertyId, companyId, newOrders);
    }

    @DeleteMapping("/{propertyId}")
    public void deleteProperty(
            @PathVariable UUID propertyId,
            @RequestParam UUID companyId
    ) {
        service.deleteProperty(propertyId, companyId);
    }
}
