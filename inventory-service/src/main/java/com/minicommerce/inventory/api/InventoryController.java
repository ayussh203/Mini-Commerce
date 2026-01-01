package com.minicommerce.inventory.api;

import com.minicommerce.inventory.domain.InventoryItem;
import com.minicommerce.inventory.domain.InventoryReservation;
import com.minicommerce.inventory.repo.InventoryItemRepository;
import com.minicommerce.inventory.repo.InventoryReservationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryItemRepository itemRepo;
    private final InventoryReservationRepository reservationRepo;

    public InventoryController(InventoryItemRepository itemRepo,
                               InventoryReservationRepository reservationRepo) {
        this.itemRepo = itemRepo;
        this.reservationRepo = reservationRepo;
    }

    @PostMapping("/reserve")
    @Transactional
    public ResponseEntity<?> reserve(@Valid @RequestBody ReserveInventoryRequest req) {

        // Idempotency check
        if (reservationRepo.existsById(req.orderId())) {
            return ResponseEntity.ok().build();
        }

        for (ReserveInventoryRequest.Item item : req.items()) {
            InventoryItem inventory =
                    itemRepo.findById(item.sku())
                            .orElseThrow(() -> new IllegalStateException("SKU not found"));

            inventory.reserve(item.qty());
            itemRepo.save(inventory);
        }

        reservationRepo.save(new InventoryReservation(req.orderId()));
        return ResponseEntity.ok().build();
    }
}
