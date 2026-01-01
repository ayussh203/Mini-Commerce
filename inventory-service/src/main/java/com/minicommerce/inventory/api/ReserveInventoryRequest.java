package com.minicommerce.inventory.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReserveInventoryRequest(
        @NotNull UUID orderId,
        @NotNull List<Item> items
) {
    public record Item(
            @NotBlank String sku,
            @Min(1) int qty
    ) {}
}
