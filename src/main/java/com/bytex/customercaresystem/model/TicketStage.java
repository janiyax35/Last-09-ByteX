package com.bytex.customercaresystem.model;

/**
 * Represents the detailed workflow stage of a support ticket, visible to customers and staff.
 */
public enum TicketStage {
    AWAITING_ACCEPTANCE("Awaiting Acceptance"),      // Newly created, waiting for staff
    WITH_STAFF("With Staff"),               // Accepted by staff, under initial review
    WITH_TECHNICIAN("With Technician"),          // Escalated to a technician for repair
    AWAITING_PARTS("Awaiting Parts"),           // Technician has requested parts, waiting for PM
    WAREHOUSE_REQUESTED("Warehouse Requested"),      // PM has forwarded request to Warehouse
    SUPPLIER_ORDERED("Supplier Ordered"),         // Warehouse has ordered parts from supplier
    RESOLVED("Resolved"),                 // The issue has been fixed
    CLOSED("Closed");                    // Ticket is closed and archived

    private final String displayName;

    TicketStage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
