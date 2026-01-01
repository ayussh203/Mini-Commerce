package com.minicommerce.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_item")
public class InventoryItem {

    @Id
    private String sku;

    @Column(name = "available_qty")
    private int availableQty;

    protected InventoryItem() {}

    public InventoryItem(String sku, int availableQty) {
        this.sku = sku;
        this.availableQty = availableQty;
    }

    public String getSku() { return sku; }
    public int getAvailableQty() { return availableQty; }

    public void reserve(int qty) {
        if (availableQty < qty) throw new IllegalStateException("Insufficient stock");
        this.availableQty -= qty;
    }
}
