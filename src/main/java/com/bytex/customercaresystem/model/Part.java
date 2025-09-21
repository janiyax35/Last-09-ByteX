package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
