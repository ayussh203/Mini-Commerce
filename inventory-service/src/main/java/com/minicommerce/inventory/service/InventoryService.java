package com.minicommerce.inventory.service;

import com.minicommerce.inventory.api.ReserveInventoryRequest;
import com.minicommerce.inventory.domain.InventoryReservation;
import com.minicommerce.inventory.repo.InventoryItemRepository;
import com.minicommerce.inventory.repo.InventoryReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryItemRepository itemRepo;
    private final InventoryReservationRepository reservationRepo;

    public InventoryService(InventoryItemRepository itemRepo, InventoryReservationRepository reservationRepo) {
        this.itemRepo = itemRepo;
        this.reservationRepo = reservationRepo;
    }

    @Transactional
    public void reserve(UUID orderId, ReserveInventoryRequest req) {
        // Idempotency: already reserved for this order -> OK, do nothing
        if (reservationRepo.existsById(orderId)) {
            return;
        }

        // Reserve all items (fail fast if any insufficient)
        for (var it : req.items()) {
            var item = itemRepo.findById(it.sku())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SKU not found: " + it.sku()));

            try {
                item.reserve(it.qty());
            } catch (IllegalStateException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for " + it.sku());
            }

            itemRepo.save(item);
        }

        // Mark reservation so retries with same orderId are no-op
        reservationRepo.save(new InventoryReservation(orderId));
    }
}
