package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "part_requests")
public class PartRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    private Repair repair;

    @Column(nullable = false)
    private int quantity = 1;

    @Lob
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartRequestStatus status = PartRequestStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime requestDate;

    private LocalDateTime fulfillmentDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }
    public User getRequestor() { return requestor; }
    public void setRequestor(User requestor) { this.requestor = requestor; }
    public Repair getRepair() { return repair; }
    public void setRepair(Repair repair) { this.repair = repair; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public PartRequestStatus getStatus() { return status; }
    public void setStatus(PartRequestStatus status) { this.status = status; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public LocalDateTime getFulfillmentDate() { return fulfillmentDate; }
    public void setFulfillmentDate(LocalDateTime fulfillmentDate) { this.fulfillmentDate = fulfillmentDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartRequest that = (PartRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
