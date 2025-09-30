package com.bytex.customercaresystem.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Constructors
    public OrderItem() {}

    public OrderItem(PurchaseOrder purchaseOrder, Part part, int quantity, BigDecimal unitPrice) {
        this.purchaseOrder = purchaseOrder;
        this.part = part;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.id = new OrderItemId(purchaseOrder.getId(), part.getId());
    }

    // Getters and Setters
    public OrderItemId getId() { return id; }
    public void setId(OrderItemId id) { this.id = id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}