package com.minicommerce.orders.client.inventory;

import java.util.List;
import java.util.UUID;

public record ReserveInventoryRequest(
        UUID orderId,
        List<Item> items
) {
    public record Item(String sku, int qty) {}
}
