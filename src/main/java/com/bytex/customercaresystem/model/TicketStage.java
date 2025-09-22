package com.bytex.customercaresystem.model;

public enum TicketStage {
    AWAITING_ACCEPTANCE("Awaiting Acceptance"),
    WITH_STAFF("With Staff"),
    WITH_TECHNICIAN("With Technician"),
    AWAITING_PARTS("Awaiting Parts"),
    WAREHOUSE_REQUESTED("Warehouse Requested"),
    SUPPLIER_ORDERED("Supplier Ordered"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayName;

    TicketStage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
