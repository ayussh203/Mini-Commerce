package com.minicommerce.inventory.api;

import com.minicommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(
            @Valid @RequestBody ReserveInventoryRequest req,
            @RequestHeader(value = "X-Correlation-Id", required = false) String cid
    ) {
        inventoryService.reserve(req.orderId(), req);
        return ResponseEntity.ok().build();
    }
}
