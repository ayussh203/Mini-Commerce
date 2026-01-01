package com.minicommerce.orders.api;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
        @NotNull List<OrderItem> items
) {
    public record OrderItem(
            @NotNull String sku,
            @NotNull Integer qty
    ) {}
}
