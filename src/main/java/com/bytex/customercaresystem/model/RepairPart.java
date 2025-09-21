package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "repair_parts")
public class RepairPart {

    @EmbeddedId
    private RepairPartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("repairId")
    @JoinColumn(name = "repair_id")
    private Repair repair;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    private int quantity = 1;

    // Constructors
    public RepairPart() {
    }

    // Getters and Setters
    public RepairPartId getId() {
        return id;
    }

    public void setId(RepairPartId id) {
        this.id = id;
    }

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairPart that = (RepairPart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RepairPart{" +
                "id=" + id +
                ", quantity=" + quantity +
                '}';
    }
}
