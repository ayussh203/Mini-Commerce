package com.minicommerce.orders.api;

import com.minicommerce.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader(value = "X-Correlation-Id", required = false) String cid
    ) {
        return ResponseEntity.ok(orderService.createOrder(req, cid));
    }
}
