package com.casaflow.property.dto;

import java.util.UUID;

public record SellerContact(
        UUID companyId,
        String companyName,
        String email,
        String phoneE164
) {
}
