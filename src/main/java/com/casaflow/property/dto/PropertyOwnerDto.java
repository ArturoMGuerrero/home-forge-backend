package com.casaflow.property.dto;

import java.util.UUID;

public record PropertyOwnerDto(
    UUID propertyId,
    String propertyCode,
    String propertyTitle,
    String ownerName,
    String ownerEmail,
    String ownerPhone,
    String ownerPhoneSecondary
) {
}
