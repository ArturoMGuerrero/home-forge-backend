package com.homeforge.property.dto;

import com.homeforge.property.domain.Property;

public record PublicPropertyResponse(
        Property property,
        SellerContact seller
) {
}
