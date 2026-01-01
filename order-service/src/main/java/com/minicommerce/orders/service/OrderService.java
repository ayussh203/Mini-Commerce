package com.minicommerce.orders.service;

import com.minicommerce.orders.api.CreateOrderRequest;
import com.minicommerce.orders.api.CreateOrderResponse;
import com.minicommerce.orders.client.inventory.ReserveInventoryRequest;
import com.minicommerce.orders.domain.OrderEntity;
import com.minicommerce.orders.domain.OrderStatus;
import com.minicommerce.orders.repo.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service
public class OrderService {

    private final WebClient inventoryClient;
    private final OrderRepository repo;

    public OrderService(WebClient inventoryClient, OrderRepository repo) {
        this.inventoryClient = inventoryClient;
        this.repo = repo;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest req, String correlationId) {
        UUID orderId = UUID.randomUUID();
        String cid = (correlationId == null || correlationId.isBlank()) ? UUID.randomUUID().toString() : correlationId;

        var reserveReq = new ReserveInventoryRequest(
                orderId,
                req.items().stream()
                        .map(i -> new ReserveInventoryRequest.Item(i.sku(), i.qty()))
                        .toList()
        );

        // 1) Reserve inventory (sync). If this fails -> DO NOT create order.
        try {
            inventoryClient.post()
                    .uri("/inventory/reserve")
                    .header("X-Correlation-Id", cid)
                    .bodyValue(reserveReq)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw ex; // will become 409 from controller
            }
            throw ex;
        }

        // 2) Save order only after inventory reserved
        repo.save(new OrderEntity(orderId, OrderStatus.PENDING_PAYMENT));

        return new CreateOrderResponse(orderId, OrderStatus.PENDING_PAYMENT.name());
    }
}
