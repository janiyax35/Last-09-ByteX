package com.bytex.customercaresystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairPartId implements Serializable {

    @Column(name = "repair_id")
    private Long repairId;

    @Column(name = "part_id")
    private Long partId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairPartId that = (RepairPartId) o;
        return Objects.equals(repairId, that.repairId) && Objects.equals(partId, that.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repairId, partId);
    }
}
