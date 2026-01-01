package com.minicommerce.inventory.api;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ReserveInventoryRequest(
        @NotNull UUID orderId,
        @NotNull List<Item> items
) {
    public record Item(String sku, int qty) {}
}
