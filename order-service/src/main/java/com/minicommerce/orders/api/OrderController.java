package com.minicommerce.orders.api;

import com.minicommerce.orders.domain.OrderEntity;
import com.minicommerce.orders.domain.OrderStatus;
import com.minicommerce.orders.repo.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repo;

    public OrderController(OrderRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@Valid @RequestBody CreateOrderRequest req,
                                                      @RequestHeader(value = "X-Correlation-Id", required = false) String cid) {
        UUID id = UUID.randomUUID();
        OrderEntity order = new OrderEntity(id, OrderStatus.PENDING_PAYMENT);
        repo.save(order);

        return ResponseEntity.ok(new CreateOrderResponse(order.getId().toString(), order.getStatus().name()));
    }
}
