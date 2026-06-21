package com.casaflow.property.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ReorderImagesRequest(
        @NotNull List<ImageOrder> images
) {
    public record ImageOrder(
            @NotNull UUID id,
            @NotNull Integer sortOrder
    ) {
    }
}
