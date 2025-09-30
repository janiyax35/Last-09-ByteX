package com.bytex.customercaresystem.models;

import com.bytex.customercaresystem.models.enums.PartRequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "part_requests")
public class PartRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PartRequestStatus status = PartRequestStatus.PENDING;

    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "fulfillment_date")
    private LocalDateTime fulfillmentDate;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
    }

    // Constructors
    public PartRequest() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }
    public User getRequestor() { return requestor; }
    public void setRequestor(User requestor) { this.requestor = requestor; }
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
}