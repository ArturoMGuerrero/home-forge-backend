package com.casaflow.property.service;

import com.casaflow.property.domain.Property;
import com.casaflow.property.domain.ListingType;
import com.casaflow.property.domain.PropertyStatus;
import com.casaflow.property.dto.CreatePropertyRequest;
import com.casaflow.property.repository.PropertyRepository;
import com.casaflow.subscription.SubscriptionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyImageStorage imageStorage;
    private final SubscriptionValidator subscriptionValidator;

    public PropertyService(PropertyRepository repository, PropertyImageStorage imageStorage, SubscriptionValidator subscriptionValidator) {
        this.repository = repository;
        this.imageStorage = imageStorage;
        this.subscriptionValidator = subscriptionValidator;
    }

    public Property create(CreatePropertyRequest request) {
        subscriptionValidator.validateActiveSubscription(request.companyId(), "crear nuevas propiedades");
        validateStatus(request.listingType(), request.status());
        Property property = new Property(
                request.companyId(), request.code(), request.title(), request.propertyType(),
                request.listingType(), request.status(), request.price(), request.currencyCode(), request.countryCode(),
                request.stateCode(), request.city(), request.address(), request.latitude(), request.longitude(),
                request.bedrooms(), request.bathrooms(), request.landArea(), request.constructionArea(),
                request.parkingSpaces(), request.description(), request.imageUrl(), request.published()
        );
        property.setOwnerName(request.ownerName());
        property.setOwnerEmail(request.ownerEmail());
        property.setOwnerPhone(request.ownerPhone());
        property.setOwnerPhoneSecondary(request.ownerPhoneSecondary());
        property.setOwnerNotes(request.ownerNotes());
        return repository.save(property);
    }

    public List<Property> byCompany(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNull(companyId);
    }

    public List<Property> getPropertiesWithOwnerContact(UUID companyId) {
        return repository.findByCompanyIdAndDeletedAtIsNull(companyId).stream()
                .filter(p -> p.getOwnerEmail() != null || p.getOwnerPhone() != null)
                .toList();
    }

    public List<Property> published() {
        return repository.findByPublishedTrueAndDeletedAtIsNullOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Property get(UUID propertyId, UUID companyId) {
        return find(propertyId, companyId);
    }

    @Transactional
    public Property update(UUID propertyId, CreatePropertyRequest request) {
        validateStatus(request.listingType(), request.status());
        Property property = find(propertyId, request.companyId());
        property.update(
                request.code(), request.title(), request.propertyType(), request.listingType(),
                request.status(), request.price(), request.currencyCode(), request.countryCode(), request.stateCode(),
                request.city(), request.address(), request.latitude(), request.longitude(), request.bedrooms(), request.bathrooms(),
                request.landArea(), request.constructionArea(), request.parkingSpaces(),
                request.description(), request.imageUrl(), request.published()
        );
        property.setOwnerName(request.ownerName());
        property.setOwnerEmail(request.ownerEmail());
        property.setOwnerPhone(request.ownerPhone());
        property.setOwnerPhoneSecondary(request.ownerPhoneSecondary());
        property.setOwnerNotes(request.ownerNotes());
        return repository.save(property);
    }

    @Transactional
    public Property addImages(UUID propertyId, UUID companyId, List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos una imagen.");
        }
        Property property = find(propertyId, companyId);
        int currentCount = property.getImages().size();
        int newCount = files.size();
        int totalCount = currentCount + newCount;

        if (totalCount > 12) {
            int remaining = 12 - currentCount;
            throw new IllegalArgumentException(
                String.format("Ya tienes %d imagen%s. Solo puedes subir %d más (máximo 12).",
                    currentCount,
                    currentCount == 1 ? "" : "es",
                    remaining)
            );
        }

        files.forEach(file -> property.addImage(imageStorage.save(propertyId, file)));
        return repository.save(property);
    }

    @Transactional
    public Property deleteImage(UUID propertyId, UUID imageId, UUID companyId) {
        Property property = find(propertyId, companyId);
        var removed = property.removeImage(imageId);
        imageStorage.delete(removed.getImageUrl());
        return repository.save(property);
    }

    @Transactional
    public Property setCover(UUID propertyId, UUID imageId, UUID companyId) {
        Property property = find(propertyId, companyId);
        property.setCoverImage(imageId);
        return repository.save(property);
    }

    @Transactional
    public Property reorderImages(UUID propertyId, UUID companyId, java.util.Map<UUID, Integer> newOrders) {
        Property property = find(propertyId, companyId);
        property.reorderImages(newOrders);
        return repository.save(property);
    }

    @Transactional
    public void deleteProperty(UUID propertyId, UUID companyId) {
        Property property = find(propertyId, companyId);

        // Eliminar todas las imágenes del filesystem ANTES de borrar la entidad
        property.getImages().forEach(image -> {
            try {
                imageStorage.delete(image.getImageUrl());
            } catch (Exception e) {
                // Log pero no fallar si una imagen no se puede borrar
                System.err.println("Error eliminando imagen " + image.getImageUrl() + ": " + e.getMessage());
            }
        });

        // Soft delete (marcar como eliminado)
        repository.delete(property);
    }

    private Property find(UUID propertyId, UUID companyId) {
        return repository.findByIdAndCompanyIdAndDeletedAtIsNull(propertyId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada."));
    }

    private static void validateStatus(ListingType listingType, PropertyStatus status) {
        if (listingType == ListingType.SALE && status == PropertyStatus.RENTED) {
            throw new IllegalArgumentException("Una propiedad en venta no puede marcarse como rentada.");
        }
        if (listingType == ListingType.RENT && status == PropertyStatus.SOLD) {
            throw new IllegalArgumentException("Una propiedad en renta no puede marcarse como vendida.");
        }
    }
}
