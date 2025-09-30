package com.bytex.customercaresystem.models;

import jakarta.persistence.*;

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

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    // Constructors
    public RepairPart() {}

    public RepairPart(Repair repair, Part part, int quantity) {
        this.repair = repair;
        this.part = part;
        this.quantity = quantity;
        this.id = new RepairPartId(repair.getId(), part.getId());
    }

    // Getters and Setters
    public RepairPartId getId() { return id; }
    public void setId(RepairPartId id) { this.id = id; }
    public Repair getRepair() { return repair; }
    public void setRepair(Repair repair) { this.repair = repair; }
    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}