package com.casaflow.property.dto;

import com.casaflow.property.domain.Property;

public record PublicPropertyResponse(
        Property property,
        SellerContact seller
) {
}
