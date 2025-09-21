package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repair_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
