package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partId;

    @Column(nullable = false, unique = true, length = 50)
    private String partNumber;

    @Column(nullable = false, length = 100)
    private String partName;

    @Lob
    private String description;

    @Column(nullable = false)
    private int currentStock = 0;

    @Column(nullable = false)
    private int minimumStock = 5;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PartStatus status = PartStatus.ACTIVE;

    @OneToMany(mappedBy = "part")
    private Set<RepairPart> repairParts;

    // Constructors
    public Part() {
    }

    // Getters and Setters
    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public PartStatus getStatus() {
        return status;
    }

    public void setStatus(PartStatus status) {
        this.status = status;
    }

    public Set<RepairPart> getRepairParts() {
        return repairParts;
    }

    public void setRepairParts(Set<RepairPart> repairParts) {
        this.repairParts = repairParts;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return Objects.equals(partId, part.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId);
    }

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", partNumber='" + partNumber + '\'' +
                ", partName='" + partName + '\'' +
                '}';
    }
}
