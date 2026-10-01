package com.homeforge.property.dto;

public record PublicPropertyResponse(
        PublicProperty property,
        SellerContact seller
) {
}
