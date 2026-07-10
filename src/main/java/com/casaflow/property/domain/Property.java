package com.casaflow.property.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class Property extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    private UUID developmentId;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 40)
    private String propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false, length = 80)
    private String stateCode;

    @Column(nullable = false, length = 120)
    private String city;

    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Integer bedrooms;
    private BigDecimal bathrooms;
    private BigDecimal landArea;
    private BigDecimal constructionArea;
    private Integer parkingSpaces;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 1000)
    private String imageUrl;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    private List<PropertyImage> images = new ArrayList<>();

    @Column(nullable = false)
    private boolean published;

    // Owner contact information
    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "owner_email", length = 100)
    private String ownerEmail;

    @Column(name = "owner_phone", length = 20)
    private String ownerPhone;

    @Column(name = "owner_phone_secondary", length = 20)
    private String ownerPhoneSecondary;

    @Column(name = "owner_notes", columnDefinition = "NVARCHAR(MAX)")
    private String ownerNotes;

    protected Property() {
    }

    public Property(
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
            boolean published
    ) {
        this.companyId = companyId;
        this.code = code;
        this.title = title;
        this.propertyType = propertyType;
        this.listingType = listingType;
        this.status = status;
        this.price = price;
        this.currencyCode = currencyCode;
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.landArea = landArea;
        this.constructionArea = constructionArea;
        this.parkingSpaces = parkingSpaces;
        this.description = description;
        this.imageUrl = imageUrl;
        this.published = published;
    }

    public UUID getCompanyId() { return companyId; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getPropertyType() { return propertyType; }
    public ListingType getListingType() { return listingType; }
    public PropertyStatus getStatus() { return status; }
    public BigDecimal getPrice() { return price; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCountryCode() { return countryCode; }
    public String getStateCode() { return stateCode; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public Integer getBedrooms() { return bedrooms; }
    public BigDecimal getBathrooms() { return bathrooms; }
    public BigDecimal getLandArea() { return landArea; }
    public BigDecimal getConstructionArea() { return constructionArea; }
    public Integer getParkingSpaces() { return parkingSpaces; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public List<PropertyImage> getImages() { return List.copyOf(images); }
    public boolean isPublished() { return published; }

    public void addImage(String imageUrl) {
        images.add(new PropertyImage(this, imageUrl, images.size()));
        if (this.imageUrl == null || this.imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
    }

    public void update(
            String code, String title, String propertyType, ListingType listingType, PropertyStatus status,
            BigDecimal price, String currencyCode, String countryCode, String stateCode,
            String city, String address, BigDecimal latitude, BigDecimal longitude, Integer bedrooms, BigDecimal bathrooms,
            BigDecimal landArea, BigDecimal constructionArea, Integer parkingSpaces,
            String description, String imageUrl, boolean published
    ) {
        this.code = code;
        this.title = title;
        this.propertyType = propertyType;
        this.listingType = listingType;
        this.status = status;
        this.price = price;
        this.currencyCode = currencyCode;
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.landArea = landArea;
        this.constructionArea = constructionArea;
        this.parkingSpaces = parkingSpaces;
        this.description = description;
        this.imageUrl = imageUrl;
        this.published = published;
    }

    public PropertyImage removeImage(UUID imageId) {
        PropertyImage image = images.stream()
                .filter(candidate -> candidate.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada."));
        images.remove(image);
        if (image.getImageUrl().equals(imageUrl)) {
            imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
        }
        return image;
    }

    public void setCoverImage(UUID imageId) {
        PropertyImage image = images.stream()
                .filter(candidate -> candidate.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada."));
        imageUrl = image.getImageUrl();
    }

    public void reorderImages(java.util.Map<UUID, Integer> newOrders) {
        if (newOrders.size() != images.size()) {
            throw new IllegalArgumentException("Debes especificar el orden de todas las imágenes.");
        }

        images.forEach(image -> {
            Integer newOrder = newOrders.get(image.getId());
            if (newOrder == null) {
                throw new IllegalArgumentException("Falta el orden para la imagen: " + image.getId());
            }
            if (newOrder < 0 || newOrder >= images.size()) {
                throw new IllegalArgumentException("Orden inválido: " + newOrder + ". Debe estar entre 0 y " + (images.size() - 1));
            }
            image.setSortOrder(newOrder);
        });
    }

    // Owner contact getters and setters
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerPhoneSecondary() {
        return ownerPhoneSecondary;
    }

    public void setOwnerPhoneSecondary(String ownerPhoneSecondary) {
        this.ownerPhoneSecondary = ownerPhoneSecondary;
    }

    public String getOwnerNotes() {
        return ownerNotes;
    }

    public void setOwnerNotes(String ownerNotes) {
        this.ownerNotes = ownerNotes;
    }
}
