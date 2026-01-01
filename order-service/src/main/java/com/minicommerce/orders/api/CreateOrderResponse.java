package com.minicommerce.orders.api;

import java.util.UUID;

public record CreateOrderResponse(UUID orderId, String status) {}
