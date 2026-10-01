package com.homeforge.property.dto;

import com.homeforge.property.domain.ListingType;
import com.homeforge.property.domain.Property;
import com.homeforge.property.domain.PropertyImage;
import com.homeforge.property.domain.PropertyStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Lo que el sitio público puede mostrar de una propiedad. Nunca incluye los datos del propietario
 * (nombre, correo, teléfono, notas): son internos de la inmobiliaria.
 */
public record PublicProperty(
        UUID id,
        UUID companyId,
        String code,
        String title,
        String propertyType,
        ListingType listingType,
        PropertyStatus status,
        BigDecimal price,
        String currencyCode,
        String countryCode,
        String stateCode,
        String city,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer bedrooms,
        BigDecimal bathrooms,
        BigDecimal landArea,
        BigDecimal constructionArea,
        Integer parkingSpaces,
        String description,
        String imageUrl,
        List<Image> images,
        Instant createdAt
) {

    public record Image(UUID id, String imageUrl, int sortOrder) {}

    public static PublicProperty from(Property property) {
        List<Image> images = property.getImages().stream()
                .sorted(Comparator.comparingInt(PropertyImage::getSortOrder))
                .map(image -> new Image(image.getId(), image.getImageUrl(), image.getSortOrder()))
                .toList();
        return new PublicProperty(
                property.getId(),
                property.getCompanyId(),
                property.getCode(),
                property.getTitle(),
                property.getPropertyType(),
                property.getListingType(),
                property.getStatus(),
                property.getPrice(),
                property.getCurrencyCode(),
                property.getCountryCode(),
                property.getStateCode(),
                property.getCity(),
                property.getAddress(),
                property.getLatitude(),
                property.getLongitude(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getLandArea(),
                property.getConstructionArea(),
                property.getParkingSpaces(),
                property.getDescription(),
                property.getImageUrl(),
                images,
                property.getCreatedAt()
        );
    }
}
