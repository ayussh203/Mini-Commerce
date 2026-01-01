package com.minicommerce.inventory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class InventoryReservation {

    @Id
    private UUID orderId;

    protected InventoryReservation() {}

    public InventoryReservation(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
