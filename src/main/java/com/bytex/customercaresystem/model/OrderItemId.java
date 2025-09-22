package com.bytex.customercaresystem.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemId implements Serializable {

    private Long orderId;
    private Long partId;

    public OrderItemId() {
    }

    public OrderItemId(Long orderId, Long partId) {
        this.orderId = orderId;
        this.partId = partId;
    }

    // Getters, Setters, equals, hashCode
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(partId, that.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, partId);
    }
}
