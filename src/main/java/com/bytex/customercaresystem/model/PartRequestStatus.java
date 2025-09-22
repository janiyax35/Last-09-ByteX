package com.bytex.customercaresystem.model;

public enum PartRequestStatus {
    PENDING,            // Awaiting PM approval
    APPROVED,           // Approved by PM (but not yet fulfilled)
    FULFILLED,          // Fulfilled by PM from stock
    REJECTED,           // Rejected by PM
    PENDING_WAREHOUSE,  // Forwarded to warehouse for procurement
    PURCHASE_ORDERED    // Warehouse has created a PO for this request
}
