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

        var reserveReq = new ReserveInventoryRequest(
                orderId.toString(),
                req.items().stream()
                        // if your item is record -> sku(), qty()
                        .map(i -> new ReserveInventoryRequest.Item(i.sku(), i.qty()))
                        .toList()
        );

        try {
            inventoryClient.post()
                    .uri("/inventory/reserve")
                    .header("X-Correlation-Id", correlationId)
                    .bodyValue(reserveReq)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw ex; // map to 409 later (stock unavailable)
            }
            throw ex;
        }

        // only after reserve succeeds -> save order
        OrderEntity order = new OrderEntity(orderId, OrderStatus.PENDING_PAYMENT);
        repo.save(order);

        return new CreateOrderResponse(orderId.toString(), order.getStatus().name());
    }
}
