package com.minicommerce.orders.client.inventory;

import java.util.List;

public record ReserveInventoryRequest(String orderId, List<Item> items) {
    public record Item(String sku, int qty) {}
}
