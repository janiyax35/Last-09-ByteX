package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.User;

public interface PartRequestService {

    /**
     * Creates a new request for a part.
     * @param requestor The user requesting the part (e.g., a Technician).
     * @param part The part being requested.
     * @param quantity The quantity requested.
     * @param reason A reason or justification for the request.
     * @return The newly created PartRequest object.
     */
    PartRequest createPartRequest(User requestor, Part part, int quantity, String reason, com.bytex.customercaresystem.model.Repair repair);

    /**
     * Finds all part requests that are pending.
     * @return A list of pending part requests.
     */
    java.util.List<PartRequest> findPendingRequests();

    /**
     * Approves a part request, deducting from stock.
     * @param requestId The ID of the request to approve.
     * @return The updated part request.
     * @throws Exception if stock is insufficient or request not found.
     */
    PartRequest approveRequest(Long requestId) throws Exception;

    /**
     * Rejects a part request.
     * @param requestId The ID of the request to reject.
     * @return The updated part request.
     * @throws Exception if the request is not found.
     */
    PartRequest rejectRequest(Long requestId) throws Exception;

    /**
     * Forwards a part request to the warehouse.
     * @param requestId The ID of the request to forward.
     * @return The updated part request.
     * @throws Exception if the request is not found.
     */
    PartRequest forwardRequestToWarehouse(Long requestId) throws Exception;

    /**
     * Finds all part requests that have been forwarded to the warehouse.
     * @return A list of pending warehouse requests.
     */
    java.util.List<PartRequest> findWarehousePendingRequests();

    /**
     * Finds a part request by its ID.
     * @param id The ID of the part request to find.
     * @return An Optional containing the part request if found.
     */
    java.util.Optional<PartRequest> findById(Long id);

    /**
     * Allows a Warehouse Manager to fulfill a request directly from existing stock.
     * @param requestId The ID of the request to fulfill.
     * @return The updated part request.
     * @throws Exception if stock is insufficient or request not found.
     */
    PartRequest fulfillRequestFromWarehouse(Long requestId) throws Exception;
}
